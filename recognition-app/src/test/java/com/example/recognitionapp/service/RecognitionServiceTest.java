package com.example.recognitionapp.service;

import com.example.recognitionapp.model.Recognition;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.repository.RecognitionRepository;
import com.example.recognitionapp.repository.UserRepository;
import com.example.recognitionapp.exception.InsufficientBalanceException;
import com.example.recognitionapp.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.ArrayList; // For List<Recognition> tests
import java.util.List;    // For List<Recognition> tests

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecognitionServiceTest {

    @Mock
    private RecognitionRepository recognitionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecognitionService recognitionService;

    private User recognizer;
    private User recipient;

    @BeforeEach
    void setUp() {
        recognizer = new User();
        recognizer.setId(1L);
        recognizer.setEmail("recognizer@example.com");
        recognizer.setAvailableCredit(BigDecimal.valueOf(100));

        recipient = new User();
        recipient.setId(2L);
        recipient.setEmail("recipient@example.com");
        recipient.setAvailableCredit(BigDecimal.valueOf(50));
    }

    @Test
    void createRecognition_success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        // Mocking the save calls for users is important
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(recognitionRepository.save(any(Recognition.class))).thenAnswer(invocation -> {
            Recognition rec = invocation.getArgument(0);
            rec.setId(1L); // Simulate saving by setting an ID
            return rec;
        });
        
        BigDecimal giftAmount = BigDecimal.valueOf(20);
        Recognition recognition = recognitionService.createRecognition(recognizer, 2L, giftAmount, "Great job!");

        assertNotNull(recognition);
        assertNotNull(recognition.getId());
        assertEquals(giftAmount, recognition.getGiftedAmount());
        assertEquals("Great job!", recognition.getMessage());
        assertEquals(BigDecimal.valueOf(80), recognizer.getAvailableCredit());
        assertEquals(BigDecimal.valueOf(70), recipient.getAvailableCredit());
        verify(userRepository, times(2)).save(any(User.class)); // recognizer and recipient
        verify(recognitionRepository).save(any(Recognition.class));
    }

    @Test
    void createRecognition_recognizerNull_throwsIllegalArgumentException() {
         assertThrows(IllegalArgumentException.class, () -> recognitionService.createRecognition(null, 2L, BigDecimal.TEN, "test"));
    }

    @Test
    void createRecognition_selfRecognition_throwsIllegalArgumentException() {
         assertThrows(IllegalArgumentException.class, () -> recognitionService.createRecognition(recognizer, recognizer.getId(), BigDecimal.TEN, "test"));
    }
    
    @Test
    void createRecognition_recipientNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> recognitionService.createRecognition(recognizer, 3L, BigDecimal.valueOf(20), "Test"));
    }

    @Test
    void createRecognition_insufficientBalance_throwsInsufficientBalanceException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));
        BigDecimal giftAmount = BigDecimal.valueOf(200); // More than recognizer has
        assertThrows(InsufficientBalanceException.class, () -> recognitionService.createRecognition(recognizer, 2L, giftAmount, "Test"));
    }

    @Test
    void getRecognitionsReceivedByUser_success() {
        when(userRepository.existsById(recipient.getId())).thenReturn(true);
        List<Recognition> expectedRecognitions = new ArrayList<>();
        // Populate with some mock recognitions
        Recognition r1 = new Recognition(); r1.setId(1L); r1.setRecipient(recipient);
        expectedRecognitions.add(r1);
        
        when(recognitionRepository.findByRecipientId(recipient.getId())).thenReturn(expectedRecognitions);
        
        List<Recognition> actualRecognitions = recognitionService.getRecognitionsReceivedByUser(recipient.getId());
        
        assertNotNull(actualRecognitions);
        assertEquals(1, actualRecognitions.size());
        assertEquals(recipient, actualRecognitions.get(0).getRecipient());
    }

    @Test
    void getRecognitionsReceivedByUser_userNotFound_throwsUserNotFoundException() {
        when(userRepository.existsById(3L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> recognitionService.getRecognitionsReceivedByUser(3L));
    }
    
    @Test
    void getRecognitionsGivenByUser_success() {
        when(userRepository.existsById(recognizer.getId())).thenReturn(true);
        List<Recognition> expectedRecognitions = new ArrayList<>();
        Recognition r1 = new Recognition(); r1.setId(1L); r1.setRecognizer(recognizer);
        expectedRecognitions.add(r1);

        when(recognitionRepository.findByRecognizerId(recognizer.getId())).thenReturn(expectedRecognitions);

        List<Recognition> actualRecognitions = recognitionService.getRecognitionsGivenByUser(recognizer.getId());

        assertNotNull(actualRecognitions);
        assertEquals(1, actualRecognitions.size());
        assertEquals(recognizer, actualRecognitions.get(0).getRecognizer());
    }

    @Test
    void getRecognitionsGivenByUser_userNotFound_throwsUserNotFoundException() {
        when(userRepository.existsById(3L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> recognitionService.getRecognitionsGivenByUser(3L));
    }
}

package com.example.recognitionapp.service;

import com.example.recognitionapp.model.Recognition;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.repository.RecognitionRepository;
import com.example.recognitionapp.repository.UserRepository;
import com.example.recognitionapp.exception.UserNotFoundException;
import com.example.recognitionapp.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecognitionService {

    private final RecognitionRepository recognitionRepository;
    private final UserRepository userRepository; // Using UserRepository directly

    @Autowired
    public RecognitionService(RecognitionRepository recognitionRepository, UserRepository userRepository) {
        this.recognitionRepository = recognitionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Recognition createRecognition(User recognizer, Long recipientId, BigDecimal amount, String message) {
        if (recognizer == null) {
            throw new IllegalArgumentException("Recognizer cannot be null.");
        }
        if (recognizer.getId().equals(recipientId)) {
            throw new IllegalArgumentException("User cannot recognize themselves.");
        }

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("Recipient user with ID " + recipientId + " not found."));

        if (recognizer.getAvailableCredit().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Recognizer does not have enough credit. Available: " + 
                                                 recognizer.getAvailableCredit() + ", Tried to gift: " + amount);
        }

        // Update balances
        recognizer.setAvailableCredit(recognizer.getAvailableCredit().subtract(amount));
        recipient.setAvailableCredit(recipient.getAvailableCredit().add(amount));

        userRepository.save(recognizer);
        userRepository.save(recipient);

        // Create and save recognition
        Recognition recognition = new Recognition();
        recognition.setRecognizer(recognizer);
        recognition.setRecipient(recipient);
        recognition.setGiftedAmount(amount);
        recognition.setMessage(message);
        recognition.setGiftedDate(LocalDateTime.now());

        return recognitionRepository.save(recognition);
    }

    public List<Recognition> getRecognitionsReceivedByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return recognitionRepository.findByRecipientId(userId);
    }

    public List<Recognition> getRecognitionsGivenByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return recognitionRepository.findByRecognizerId(userId);
    }
}

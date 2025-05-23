package com.example.recognitionapp.service;

import com.example.recognitionapp.model.Org;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.model.RedemptionHistory;
import com.example.recognitionapp.repository.UserRepository;
import com.example.recognitionapp.repository.OrgRepository;
import com.example.recognitionapp.repository.RedemptionHistoryRepository;
import com.example.recognitionapp.exception.UserAlreadyExistsException;
import com.example.recognitionapp.exception.OrgNotFoundException;
import com.example.recognitionapp.exception.InsufficientBalanceException;
import com.example.recognitionapp.exception.UserNotFoundException; // Custom
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails; // For testing loadUserByUsername
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Spring's exception
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrgRepository orgRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RedemptionHistoryRepository redemptionHistoryRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Org org;

    @BeforeEach
    void setUp() {
        org = new Org();
        org.setId(1L);
        org.setName("Test Org");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password"); // Raw password before encoding
        user.setOrg(org);
        user.setAvailableCredit(BigDecimal.valueOf(100));
        user.setStatus("ACTIVE");
    }

    @Test
    void registerUser_success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(orgRepository.findById(1L)).thenReturn(Optional.of(org));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // Capture the user argument passed to save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Simulate DB generating ID if it's a new user without ID
            if (savedUser.getId() == null) {
                savedUser.setId(2L); // Assign a new ID
            }
            return savedUser;
        });

        User newUserDetails = new User(); // This is the input DTO-like object, not an entity yet
        newUserDetails.setEmail("new@example.com");
        newUserDetails.setPassword("password");
        
        User registeredUser = userService.registerUser(newUserDetails, 1L);

        assertNotNull(registeredUser);
        assertEquals("new@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(BigDecimal.ZERO, registeredUser.getAvailableCredit());
        assertEquals("ACTIVE", registeredUser.getStatus());
        assertNotNull(registeredUser.getOrg());
        assertEquals(1L, registeredUser.getOrg().getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_emailExists_throwsUserAlreadyExistsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        
        User newUserDetails = new User();
        newUserDetails.setEmail("test@example.com");
        newUserDetails.setPassword("password");

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(newUserDetails, 1L));
    }

    @Test
    void registerUser_orgNotFound_throwsOrgNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(orgRepository.findById(1L)).thenReturn(Optional.empty());
        
        User newUserDetails = new User();
        newUserDetails.setEmail("new@example.com");
        newUserDetails.setPassword("password");

        assertThrows(OrgNotFoundException.class, () -> userService.registerUser(newUserDetails, 1L));
    }
    
    @Test
    void loadUserByUsername_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        // UserDetails interface methods are tested in User.java if it implements UserDetails
        // Here we just ensure the correct user is fetched.
    }

    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown@example.com"));
    }

    @Test
    void getUserBalance_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        BigDecimal balance = userService.getUserBalance(1L);
        assertEquals(BigDecimal.valueOf(100), balance);
    }
    
    @Test
    void getUserBalance_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserBalance(1L));
    }

    @Test
    void redeemBalance_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user); // User save mock
        when(redemptionHistoryRepository.save(any(RedemptionHistory.class))).thenAnswer(i -> {
            RedemptionHistory rh = i.getArgument(0);
            rh.setId(1L); // Simulate DB setting ID
            return rh;
        });

        BigDecimal amountToRedeem = BigDecimal.valueOf(50);
        RedemptionHistory redemption = userService.redeemBalance(1L, amountToRedeem);

        assertNotNull(redemption);
        assertEquals(BigDecimal.valueOf(50), user.getAvailableCredit()); // 100 - 50
        assertEquals(amountToRedeem, redemption.getRedeemedAmount());
        assertEquals(user, redemption.getUser());
        assertNotNull(redemption.getRedeemedDate());
        verify(redemptionHistoryRepository).save(any(RedemptionHistory.class));
        verify(userRepository).save(user); // Verify user's updated balance is saved
    }

    @Test
    void redeemBalance_insufficientBalance_throwsInsufficientBalanceException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        BigDecimal amountToRedeem = BigDecimal.valueOf(200); // User has 100
        assertThrows(InsufficientBalanceException.class, () -> userService.redeemBalance(1L, amountToRedeem));
    }
    
    @Test
    void redeemBalance_negativeAmount_throwsIllegalArgumentException() {
        // No need to mock findById for this, as the check is before DB interaction for amount
        // However, if service method fetches user first, then mock it.
        // Based on current UserService, it fetches user first.
        when(userRepository.findById(1L)).thenReturn(Optional.of(user)); 
        BigDecimal amountToRedeem = BigDecimal.valueOf(-10);
        assertThrows(IllegalArgumentException.class, () -> userService.redeemBalance(1L, amountToRedeem));
    }

    @Test
    void redeemBalance_zeroAmount_throwsIllegalArgumentException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        BigDecimal amountToRedeem = BigDecimal.ZERO;
        assertThrows(IllegalArgumentException.class, () -> userService.redeemBalance(1L, amountToRedeem));
    }
}

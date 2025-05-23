package com.example.recognitionapp.service;

import com.example.recognitionapp.model.User;
import com.example.recognitionapp.model.Org;
import com.example.recognitionapp.repository.UserRepository;
import com.example.recognitionapp.repository.OrgRepository;
import com.example.recognitionapp.exception.UserAlreadyExistsException;
import com.example.recognitionapp.exception.OrgNotFoundException;
import com.example.recognitionapp.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // New import
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime; // New import
import com.example.recognitionapp.repository.RedemptionHistoryRepository;
import com.example.recognitionapp.model.RedemptionHistory;
import com.example.recognitionapp.exception.InsufficientBalanceException;
import java.util.List; // New import

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedemptionHistoryRepository redemptionHistoryRepository; // Add this

    @Autowired
    public UserService(UserRepository userRepository, OrgRepository orgRepository, 
                       PasswordEncoder passwordEncoder, RedemptionHistoryRepository redemptionHistoryRepository) { // Add RedemptionHistoryRepository
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.passwordEncoder = passwordEncoder;
        this.redemptionHistoryRepository = redemptionHistoryRepository; // Initialize
    }

    public User registerUser(User user, Long orgId) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }
        Org org = orgRepository.findById(orgId)
                .orElseThrow(() -> new OrgNotFoundException("Organization with ID " + orgId + " not found."));
        
        user.setOrg(org);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAvailableCredit(BigDecimal.ZERO); // Default balance
        user.setStatus("ACTIVE");
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional // Add transactional if not already on class level and modifying entities
    public User updateUser(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User with ID " + (user.getId() == null ? "null" : user.getId()) + " not found.");
        }
        // Consider if password needs re-encoding if changed through this method
        return userRepository.save(user);
    }

    public BigDecimal getUserBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        return user.getAvailableCredit();
    }

    @Transactional
    public RedemptionHistory redeemBalance(Long userId, BigDecimal amountToRedeem) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        if (user.getAvailableCredit().compareTo(amountToRedeem) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to redeem. Available: " + 
                                                 user.getAvailableCredit() + ", Tried to redeem: " + amountToRedeem);
        }
        if (amountToRedeem.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Redemption amount must be positive.");
        }

        user.setAvailableCredit(user.getAvailableCredit().subtract(amountToRedeem));
        userRepository.save(user);

        RedemptionHistory redemption = new RedemptionHistory();
        redemption.setUser(user);
        redemption.setRedeemedAmount(amountToRedeem);
        redemption.setRedeemedDate(LocalDateTime.now());

        return redemptionHistoryRepository.save(redemption);
    }
}

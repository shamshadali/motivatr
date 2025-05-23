package com.example.recognitionapp.controller;

import com.example.recognitionapp.dto.UserRegistrationRequest;
import com.example.recognitionapp.dto.UserResponse;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.service.UserService;
import com.example.recognitionapp.exception.UserAlreadyExistsException;
import com.example.recognitionapp.exception.OrgNotFoundException;
import com.example.recognitionapp.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import com.example.recognitionapp.dto.RedemptionRequest; // New import
import com.example.recognitionapp.dto.BalanceResponse; // New import
import com.example.recognitionapp.model.RedemptionHistory; // New import
import com.example.recognitionapp.exception.InsufficientBalanceException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.math.BigDecimal;
import java.util.List; // New import
import java.util.stream.Collectors; // New import

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam("query") String query) {
        List<User> users = userService.searchUsers(query);
        List<UserResponse> userResponses = users.stream()
                                                 .map(UserResponse::fromEntity)
                                                 .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            User newUser = new User();
            newUser.setFirstName(registrationRequest.getFirstName());
            newUser.setLastName(registrationRequest.getLastName());
            newUser.setEmail(registrationRequest.getEmail());
            newUser.setPassword(registrationRequest.getPassword()); // Password will be plain text for now

            User registeredUser = userService.registerUser(newUser, registrationRequest.getOrgId());
            return new ResponseEntity<>(UserResponse.fromEntity(registeredUser), HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (OrgNotFoundException e) { 
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { 
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(u -> ResponseEntity.ok(UserResponse.fromEntity(u)))
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findUserByEmail(email);
        return user.map(u -> ResponseEntity.ok(UserResponse.fromEntity(u)))
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/balance")
    public ResponseEntity<?> getCurrentUserBalance(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        try {
            BigDecimal balance = userService.getUserBalance(currentUser.getId());
            return ResponseEntity.ok(new BalanceResponse(balance)); // Or return balance directly
        } catch (UserNotFoundException e) {
            // This case should ideally not happen if @AuthenticationPrincipal provides a valid user
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/me/redeem")
    public ResponseEntity<?> redeemCurrentUserBalance(@RequestBody RedemptionRequest request,
                                                      @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        try {
            RedemptionHistory redemption = userService.redeemBalance(currentUser.getId(), request.getAmount());
            return ResponseEntity.ok(redemption);
        } catch (UserNotFoundException e) {
             // This case should ideally not happen
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InsufficientBalanceException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.example.recognitionapp.controller;

import com.example.recognitionapp.dto.RecognitionRequest;
import com.example.recognitionapp.model.Recognition;
import com.example.recognitionapp.model.User;
import com.example.recognitionapp.service.RecognitionService;
import com.example.recognitionapp.exception.UserNotFoundException;
import com.example.recognitionapp.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recognitions")
public class RecognitionController {

    private final RecognitionService recognitionService;

    @Autowired
    public RecognitionController(RecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendRecognition(@RequestBody RecognitionRequest request, 
                                             @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        try {
            Recognition recognition = recognitionService.createRecognition(
                    currentUser, 
                    request.getRecipientId(), 
                    request.getAmount(), 
                    request.getMessage()
            );
            return new ResponseEntity<>(recognition, HttpStatus.CREATED);
        } catch (UserNotFoundException | InsufficientBalanceException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedRecognitions(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<Recognition> recognitions = recognitionService.getRecognitionsReceivedByUser(currentUser.getId());
            return ResponseEntity.ok(recognitions);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/given")
    public ResponseEntity<?> getGivenRecognitions(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        try {
            List<Recognition> recognitions = recognitionService.getRecognitionsGivenByUser(currentUser.getId());
            return ResponseEntity.ok(recognitions);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

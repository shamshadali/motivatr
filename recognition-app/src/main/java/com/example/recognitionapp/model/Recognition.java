package com.example.recognitionapp.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recognitions")
public class Recognition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recognizer_user_id")
    private User recognizer;

    private BigDecimal giftedAmount;
    private LocalDateTime giftedDate;

    @Column(nullable = true)
    private String message;

    // Default constructor
    public Recognition() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getRecognizer() {
        return recognizer;
    }

    public void setRecognizer(User recognizer) {
        this.recognizer = recognizer;
    }

    public BigDecimal getGiftedAmount() {
        return giftedAmount;
    }

    public void setGiftedAmount(BigDecimal giftedAmount) {
        this.giftedAmount = giftedAmount;
    }

    public LocalDateTime getGiftedDate() {
        return giftedDate;
    }

    public void setGiftedDate(LocalDateTime giftedDate) {
        this.giftedDate = giftedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

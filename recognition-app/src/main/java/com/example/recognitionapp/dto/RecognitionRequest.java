package com.example.recognitionapp.dto;

import java.math.BigDecimal;
// Add appropriate validation annotations later if needed (e.g., @NotNull, @Positive)
public class RecognitionRequest {
    private Long recipientId;
    private BigDecimal amount;
    private String message;
    // Getters and Setters
    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

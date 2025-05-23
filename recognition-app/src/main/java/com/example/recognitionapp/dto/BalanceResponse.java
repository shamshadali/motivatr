package com.example.recognitionapp.dto;
import java.math.BigDecimal;
public class BalanceResponse {
    private BigDecimal balance;
    public BalanceResponse(BigDecimal balance) { this.balance = balance; }
    // Getter and Setter
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}

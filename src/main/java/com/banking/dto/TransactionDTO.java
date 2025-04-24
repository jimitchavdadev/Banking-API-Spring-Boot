package com.banking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long transactionId;

    @NotNull(message = "Account ID is mandatory", groups = CreateValidation.class)
    private Integer accountId;

    @NotNull(message = "Transaction type is mandatory", groups = CreateValidation.class)
    private TransactionType transactionType;

    @NotNull(message = "Amount is mandatory", groups = CreateValidation.class)
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDateTime transactionDate;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    private Integer targetAccountId;

    // Validation group for create operations
    public interface CreateValidation {}

    // Constructors
    public TransactionDTO() {}

    public TransactionDTO(Long transactionId, Integer accountId, TransactionType transactionType,
                          BigDecimal amount, LocalDateTime transactionDate, String description,
                          Integer targetAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.targetAccountId = targetAccountId;
    }

    // Getters and Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getTargetAccountId() { return targetAccountId; }
    public void setTargetAccountId(Integer targetAccountId) { this.targetAccountId = targetAccountId; }
}
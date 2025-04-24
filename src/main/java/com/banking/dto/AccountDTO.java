package com.banking.dto;

import com.banking.entity.Account;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class AccountDTO {
    private Integer accountId;

    @NotNull(message = "Customer ID is mandatory", groups = CreateValidation.class)
    private Integer customerId;

    @NotNull(message = "Branch ID is mandatory", groups = CreateValidation.class)
    private Integer branchId;

    @NotNull(message = "Account type is mandatory")
    private Account.AccountType accountType;

    @NotBlank(message = "Account number is mandatory", groups = CreateValidation.class)
    @Size(max = 20, message = "Account number must be less than 20 characters")
    private String accountNumber;

    @NotNull(message = "Balance is mandatory")
    @DecimalMin(value = "0.00", message = "Balance must be non-negative")
    private BigDecimal balance;

    private Account.AccountStatus status;

    // Validation group for create operations
    public interface CreateValidation {
    }

    // Constructors
    public AccountDTO() {
    }

    public AccountDTO(Integer accountId, Integer customerId, Integer branchId, Account.AccountType accountType,
                      String accountNumber, BigDecimal balance, Account.AccountStatus status) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.branchId = branchId;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Account.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Account.AccountStatus getStatus() {
        return status;
    }

    public void setStatus(Account.AccountStatus status) {
        this.status = status;
    }
}
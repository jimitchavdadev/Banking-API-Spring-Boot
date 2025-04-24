package com.banking.dto;

import com.banking.entity.Account;

import java.math.BigDecimal;

public class AccountSummaryDTO {
    private Integer accountId;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private Integer customerId;
    private String firstName;
    private String lastName;
    private String branchName;

    // Constructors
    public AccountSummaryDTO() {
    }

    public AccountSummaryDTO(Integer accountId, String accountNumber, Account.AccountType accountType,
                             BigDecimal balance, Integer customerId, String firstName, String lastName, String branchName) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchName = branchName;
    }

    // Getters and Setters
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Account.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
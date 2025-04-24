package com.banking.services;

import com.banking.dto.AccountSummaryDTO;
import com.banking.entity.AccountSummary;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountSummaryService {

    @Autowired
    private AccountSummaryRepository accountSummaryRepository;

    // Get all account summaries
    public List<AccountSummaryDTO> getAllAccountSummaries() {
        return accountSummaryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get account summary by account ID
    public AccountSummaryDTO getAccountSummaryById(Integer accountId) {
        AccountSummary summary = accountSummaryRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account summary not found with id: " + accountId));
        return convertToDTO(summary);
    }

    // Get account summaries by customer ID
    public List<AccountSummaryDTO> getAccountSummariesByCustomerId(Integer customerId) {
        List<AccountSummary> summaries = accountSummaryRepository.findByCustomerId(customerId);
        if (summaries.isEmpty()) {
            throw new ResourceNotFoundException("No account summaries found for customer id: " + customerId);
        }
        return summaries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Entity to DTO
    private AccountSummaryDTO convertToDTO(AccountSummary summary) {
        return new AccountSummaryDTO(
                summary.getAccountId(),
                summary.getAccountNumber(),
                summary.getAccountType(),
                summary.getBalance(),
                summary.getCustomerId(),
                summary.getFirstName(),
                summary.getLastName(),
                summary.getBranchName()
        );
    }
}
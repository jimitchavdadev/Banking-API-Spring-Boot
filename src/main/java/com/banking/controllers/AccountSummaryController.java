package com.banking.controllers;

import com.banking.dto.AccountSummaryDTO;
import com.banking.services.AccountSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account-summary")
public class AccountSummaryController {

    @Autowired
    private AccountSummaryService accountSummaryService;

    // Get all account summaries
    @GetMapping
    public List<AccountSummaryDTO> getAllAccountSummaries() {
        return accountSummaryService.getAllAccountSummaries();
    }

    // Get account summary by account ID
    @GetMapping("/{account_id}")
    public ResponseEntity<AccountSummaryDTO> getAccountSummaryById(@PathVariable("account_id") Integer accountId) {
        return ResponseEntity.ok(accountSummaryService.getAccountSummaryById(accountId));
    }

    // Get account summaries by customer ID
    @GetMapping("/customer/{customer_id}")
    public List<AccountSummaryDTO> getAccountSummariesByCustomerId(@PathVariable("customer_id") Integer customerId) {
        return accountSummaryService.getAccountSummariesByCustomerId(customerId);
    }
}
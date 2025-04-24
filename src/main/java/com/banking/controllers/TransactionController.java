package com.banking.controllers;

import com.banking.dto.TransactionDTO;
import com.banking.services.TransactionService;
import jakarta.validation.groups.Default;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Get all transactions
    @GetMapping
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Get transaction by ID
    @GetMapping("/{transaction_id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable("transaction_id") Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    // Get transactions by account ID
    @GetMapping("/account/{account_id}")
    public List<TransactionDTO> getTransactionsByAccountId(@PathVariable("account_id") Integer accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    // Get transactions by date range
    @GetMapping("/date")
    public List<TransactionDTO> getTransactionsByDateRange(
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return transactionService.getTransactionsByDateRange(startDate, endDate);
    }

    // Record a deposit
    @PostMapping("/deposit")
    @Validated({TransactionDTO.CreateValidation.class, Default.class})
    public ResponseEntity<TransactionDTO> deposit(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.deposit(transactionDTO));
    }

    // Record a withdrawal
    @PostMapping("/withdrawal")
    @Validated({TransactionDTO.CreateValidation.class, Default.class})
    public ResponseEntity<TransactionDTO> withdraw(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.withdraw(transactionDTO));
    }

    // Perform a transfer
    @PostMapping("/transfer")
    @Validated({TransactionDTO.CreateValidation.class, Default.class})
    public ResponseEntity<TransactionDTO> transfer(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.transfer(transactionDTO));
    }
}
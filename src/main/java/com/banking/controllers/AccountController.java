package com.banking.controllers;

import com.banking.dto.AccountDTO;
import com.banking.exception.ResourceNotFoundException;
import com.banking.services.AccountService;
import jakarta.validation.groups.Default;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{account_id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable("account_id") Integer accountId) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @GetMapping("/customer/{customer_id}")
    public List<AccountDTO> getAccountsByCustomerId(@PathVariable("customer_id") Integer customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }

    @GetMapping("/number/{account_number}")
    public ResponseEntity<AccountDTO> getAccountByAccountNumber(@PathVariable("account_number") String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByAccountNumber(accountNumber));
    }

    @PostMapping
    @Validated({AccountDTO.CreateValidation.class, Default.class})
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    @PutMapping("/{account_id}")
    @Validated(Default.class)
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable("account_id") Integer accountId,
                                                    @Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, accountDTO));
    }

    @DeleteMapping("/{account_id}")
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable("account_id") Integer accountId) {
        try {
            accountService.deleteAccount(accountId);
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Account deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
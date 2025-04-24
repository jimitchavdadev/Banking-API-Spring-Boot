package com.banking.services;

import com.banking.dto.TransactionDTO;
import com.banking.dto.TransactionType;
import com.banking.entity.Account;
import com.banking.entity.Transaction;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Get all transactions
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transaction by ID
    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return convertToDTO(transaction);
    }

    // Get transactions by account ID
    public List<TransactionDTO> getTransactionsByAccountId(Integer accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return transactionRepository.findByAccountAccountId(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get transactions by date range
    public List<TransactionDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return transactionRepository.findByTransactionDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Record a deposit
    @Transactional
    public TransactionDTO deposit(TransactionDTO transactionDTO) {
        validateTransactionDTO(transactionDTO, TransactionType.DEPOSIT);
        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId()));

        // Update account balance
        account.setBalance(account.getBalance().add(transactionDTO.getAmount()));
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = convertToEntity(transactionDTO);
        transaction.setAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    // Record a withdrawal
    @Transactional
    public TransactionDTO withdraw(TransactionDTO transactionDTO) {
        validateTransactionDTO(transactionDTO, TransactionType.WITHDRAWAL);
        Account account = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + transactionDTO.getAccountId()));

        // Check sufficient balance
        if (account.getBalance().compareTo(transactionDTO.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }

        // Update account balance
        account.setBalance(account.getBalance().subtract(transactionDTO.getAmount()));
        accountRepository.save(account);

        // Record transaction
        Transaction transaction = convertToEntity(transactionDTO);
        transaction.setAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    // Perform a transfer using transfer_money stored procedure
    @Transactional
    public TransactionDTO transfer(TransactionDTO transactionDTO) {
        if (transactionDTO.getTargetAccountId() == null) {
            throw new IllegalArgumentException("Target account ID is mandatory for transfer");
        }
        if (transactionDTO.getAccountId().equals(transactionDTO.getTargetAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        validateTransactionDTO(transactionDTO, TransactionType.TRANSFER);

        Account fromAccount = accountRepository.findById(transactionDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found with id: " + transactionDTO.getAccountId()));
        Account toAccount = accountRepository.findById(transactionDTO.getTargetAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found with id: " + transactionDTO.getTargetAccountId()));

        // Call stored procedure with description
        String description = transactionDTO.getDescription() != null ? transactionDTO.getDescription() : "Transfer to account " + transactionDTO.getTargetAccountId();
        try {
            entityManager.createNativeQuery("CALL transfer_money(:from_account_id, :to_account_id, :amount, :description)")
                    .setParameter("from_account_id", transactionDTO.getAccountId())
                    .setParameter("to_account_id", transactionDTO.getTargetAccountId())
                    .setParameter("amount", transactionDTO.getAmount())
                    .setParameter("description", description)
                    .executeUpdate();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.contains("Insufficient funds")) {
                throw new IllegalStateException("Insufficient funds for transfer");
            }
            throw new IllegalStateException("Transfer failed: " + message);
        }

        // Retrieve the latest transfer transaction for the from_account
        Transaction transaction = transactionRepository.findByAccountAccountId(transactionDTO.getAccountId())
                .stream()
                .filter(t -> t.getTransactionType() == TransactionType.TRANSFER
                        && t.getTargetAccount() != null
                        && t.getTargetAccount().getAccountId().equals(transactionDTO.getTargetAccountId())
                        && t.getAmount().equals(transactionDTO.getAmount()))
                .max((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
                .orElseThrow(() -> new IllegalStateException("Transfer transaction not found"));
        return convertToDTO(transaction);
    }


    // Validate transaction DTO
    private void validateTransactionDTO(TransactionDTO transactionDTO, TransactionType expectedType) {
        if (transactionDTO.getTransactionType() != expectedType) {
            throw new IllegalArgumentException("Invalid transaction type: expected " + expectedType);
        }
        if (transactionDTO.getAmount() == null || transactionDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    // Convert Entity to DTO
    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getTransactionId(),
                transaction.getAccount().getAccountId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getAccountId() : null
        );
    }

    // Convert DTO to Entity
    private Transaction convertToEntity(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        return transaction;
    }
}
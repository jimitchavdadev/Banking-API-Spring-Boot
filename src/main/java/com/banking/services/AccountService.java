package com.banking.services;

import com.banking.dto.AccountDTO;
import com.banking.entity.Account;
import com.banking.entity.Branch;
import com.banking.entity.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.BranchRepository;
import com.banking.repository.CustomerRepository;
import com.banking.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return convertToDTO(account);
    }

    public List<AccountDTO> getAccountsByCustomerId(Integer customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return accountRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
        return convertToDTO(account);
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId()));
        Branch branch = branchRepository.findById(accountDTO.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + accountDTO.getBranchId()));
        if (accountRepository.findByAccountNumber(accountDTO.getAccountNumber()).isPresent()) {
            throw new IllegalArgumentException("Account number already exists: " + accountDTO.getAccountNumber());
        }

        Account account = convertToEntity(accountDTO);
        account.setCustomer(customer);
        account.setBranch(branch);

        Account savedAccount = accountRepository.save(account);
        return convertToDTO(savedAccount);
    }

    @Transactional
    public AccountDTO updateAccount(Integer accountId, AccountDTO accountDTO) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setAccountType(accountDTO.getAccountType());
        account.setStatus(accountDTO.getStatus());
        if (accountDTO.getBalance() != null) {
            account.setBalance(accountDTO.getBalance());
        }

        Account updatedAccount = accountRepository.save(account);
        return convertToDTO(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot delete account: Balance must be zero.");
        }

        if (accountRepository.hasAssociatedTransactions(accountId)) {
            throw new IllegalStateException("Cannot delete account: It has associated transactions.");
        }

        accountRepository.delete(account);
    }

    private AccountDTO convertToDTO(Account account) {
        return new AccountDTO(
                account.getAccountId(),
                account.getCustomer().getCustomerId(),
                account.getBranch().getBranchId(),
                account.getAccountType(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus()
        );
    }

    private Account convertToEntity(AccountDTO accountDTO) {
        Account account = new Account();
        account.setAccountId(accountDTO.getAccountId());
        account.setAccountType(accountDTO.getAccountType());
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setBalance(accountDTO.getBalance());
        account.setStatus(accountDTO.getStatus());
        return account;
    }
}
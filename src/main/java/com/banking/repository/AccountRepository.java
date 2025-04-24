package com.banking.repository;

import com.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerCustomerId(Integer customerId);

    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.account.accountId = :accountId")
    boolean hasAssociatedTransactions(@Param("accountId") Integer accountId);
}
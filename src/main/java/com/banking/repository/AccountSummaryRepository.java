package com.banking.repository;

import com.banking.entity.AccountSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountSummaryRepository extends JpaRepository<AccountSummary, Integer> {
    List<AccountSummary> findByCustomerId(Integer customerId);
}
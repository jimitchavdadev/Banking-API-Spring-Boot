package com.banking.repository;

import com.banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.customer.customerId = :customerId")
    boolean hasAssociatedAccounts(@Param("customerId") Integer customerId);
}
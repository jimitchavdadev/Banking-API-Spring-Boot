package com.banking.controllers;

import com.banking.dto.CustomerDTO;
import com.banking.exception.ResourceNotFoundException;
import com.banking.services.CustomerService;
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
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Get all customers
    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // Get customer by ID
    @GetMapping("/{customer_id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable("customer_id") Integer customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    // Get customer by email
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    // Create a new customer
    @PostMapping
    @Validated({CustomerDTO.CreateValidation.class, Default.class})
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.createCustomer(customerDTO));
    }

    // Update an existing customer
    @PutMapping("/{customer_id}")
    @Validated(Default.class)
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable("customer_id") Integer customerId,
                                                      @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerDTO));
    }

    // Delete a customer
    @DeleteMapping("/{customer_id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable("customer_id") Integer customerId) {
        try {
            customerService.deleteCustomer(customerId);
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
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
package com.banking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CustomerDTO {
    private Integer customerId;

    @NotBlank(message = "First name is mandatory", groups = CreateValidation.class)
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory", groups = CreateValidation.class)
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @NotBlank(message = "Email is mandatory", groups = CreateValidation.class)
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Size(max = 15, message = "Phone must be less than 15 characters")
    private String phone;

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    private LocalDate dateOfBirth;

    // Validation group for create operations
    public interface CreateValidation {}

    // Constructors
    public CustomerDTO() {}

    public CustomerDTO(Integer customerId, String firstName, String lastName, String email,
                       String phone, String address, LocalDate dateOfBirth) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
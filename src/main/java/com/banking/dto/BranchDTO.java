package com.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BranchDTO {

    private Integer branchId;

    @NotBlank(message = "Branch name is mandatory")
    @Size(max = 100, message = "Branch name must be less than 100 characters")
    private String branchName;

    @NotBlank(message = "Branch address is mandatory")
    @Size(max = 255, message = "Branch address must be less than 255 characters")
    private String branchAddress;

    @Size(max = 15, message = "Branch phone must be less than 15 characters")
    private String branchPhone;

    // Constructors
    public BranchDTO() {
    }

    public BranchDTO(Integer branchId, String branchName, String branchAddress, String branchPhone) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.branchPhone = branchPhone;
    }

    // Getters and Setters
    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchPhone() {
        return branchPhone;
    }

    public void setBranchPhone(String branchPhone) {
        this.branchPhone = branchPhone;
    }

    public enum AccountType {
        SAVINGS,
        CHECKING
    }
}
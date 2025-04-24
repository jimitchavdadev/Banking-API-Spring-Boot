package com.banking.controllers;

import com.banking.dto.BranchDTO;
import com.banking.entity.Branch;
import com.banking.repository.BranchRepository;
import com.banking.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchRepository branchRepository;

    // GET: Retrieve all branches
    @GetMapping
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // GET: Retrieve a specific branch by ID
    @GetMapping("/{branch_id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable("branch_id") Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
        return ResponseEntity.ok(convertToDTO(branch));
    }

    // POST: Create a new branch
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody BranchDTO branchDTO) {
        Branch branch = convertToEntity(branchDTO);
        Branch savedBranch = branchRepository.save(branch);
        return ResponseEntity.ok(convertToDTO(savedBranch));
    }

    // PUT: Update an existing branch
    @PutMapping("/{branch_id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable("branch_id") Integer branchId,
                                                  @Valid @RequestBody BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchAddress(branchDTO.getBranchAddress());
        branch.setBranchPhone(branchDTO.getBranchPhone());

        Branch updatedBranch = branchRepository.save(branch);
        return ResponseEntity.ok(convertToDTO(updatedBranch));
    }

    // DELETE: Delete a branch
    @DeleteMapping("/{branch_id}")
    public ResponseEntity<String> deleteBranch(@PathVariable("branch_id") Integer branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));

        try {
            branchRepository.delete(branch);
            return ResponseEntity.ok("Branch deleted successfully");
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete branch due to existing accounts");
        }
    }

    // Helper methods to convert between Entity and DTO
    private BranchDTO convertToDTO(Branch branch) {
        return new BranchDTO(
                branch.getBranchId(),
                branch.getBranchName(),
                branch.getBranchAddress(),
                branch.getBranchPhone()
        );
    }

    private Branch convertToEntity(BranchDTO branchDTO) {
        Branch branch = new Branch();
        branch.setBranchId(branchDTO.getBranchId());
        branch.setBranchName(branchDTO.getBranchName());
        branch.setBranchAddress(branchDTO.getBranchAddress());
        branch.setBranchPhone(branchDTO.getBranchPhone());
        return branch;
    }
}
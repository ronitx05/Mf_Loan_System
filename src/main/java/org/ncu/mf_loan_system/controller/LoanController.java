package org.ncu.mf_loan_system.controller;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("isAuthenticated()") // Base security for all endpoints
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan) {
        return ResponseEntity.status(201).body(loanService.createLoan(loan));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER')")
    public ResponseEntity<Loan> updateLoan(@PathVariable Long id, @RequestBody Loan loan) {
        return ResponseEntity.ok(loanService.updateLoan(id, loan));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/outstanding")
    @PreAuthorize("hasAnyRole('MANAGER', 'AUDITOR')")
    public ResponseEntity<BigDecimal> getTotalOutstanding() {
        return ResponseEntity.ok(loanService.getTotalOutstandingAmount());
    }
}
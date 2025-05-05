package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import java.util.List;

public interface LoanService {
    List<Loan> getAllLoans();
    Loan getLoanById(Long id);
    Loan createLoan(Loan loan);
    Loan updateLoan(Long id, Loan loan);
    void deleteLoan(Long id);
}
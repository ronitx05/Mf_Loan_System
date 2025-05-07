package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    List<Loan> getAllLoans();
    Loan getLoanById(Long id);
    Loan createLoan(Loan loan);
    Loan updateLoan(Long id, Loan loan);
    void deleteLoan(Long id);
    BigDecimal getTotalOutstandingAmount();
    BigDecimal calculateEMI(Long loanId);
    void processPayment(Long loanId, BigDecimal amount);

    List<Loan> getLoansByClientId(Long clientId);
}
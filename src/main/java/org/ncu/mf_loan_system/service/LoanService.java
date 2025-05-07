package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.repository.LoanRepository;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {

    public default void issueLoan(String clientId, double amount) {

    }

    public default void checkOverdueLoan(Long loanId) {

    }
    List<Loan> getAllLoans();
    Loan getLoanById(Long id);
    Loan createLoan(Loan loan);
    Loan updateLoan(Long id, Loan loan);
    void deleteLoan(Long id);
    BigDecimal getTotalOutstandingAmount();

}
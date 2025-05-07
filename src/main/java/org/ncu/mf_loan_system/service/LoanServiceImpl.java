package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
    }

    @Override
    @Transactional
    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan updateLoan(Long id, Loan loan) {
        Loan existing = getLoanById(id);
        existing.setPrincipalAmount(loan.getPrincipalAmount());
        existing.setInterestRate(loan.getInterestRate());
        existing.setStartDate(loan.getStartDate());
        existing.setEndDate(loan.getEndDate());
        return loanRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteLoan(Long id) {
        Loan loan = getLoanById(id);
        loanRepository.delete(loan);
    }


    @Override
    public BigDecimal getTotalOutstandingAmount() {
        List<Loan> allLoans = loanRepository.findAll();
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        for (Loan loan : allLoans) {
            BigDecimal loanAmount = loan.getPrincipalAmount(); // assuming full amount is pending
            BigDecimal paidAmount = loan.getPayments().stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal outstanding = loanAmount.subtract(paidAmount);
            totalOutstanding = totalOutstanding.add(outstanding);
        }

        return totalOutstanding;
    }
}
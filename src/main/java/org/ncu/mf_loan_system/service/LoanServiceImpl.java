package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.*;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
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
        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan == null) {
            throw new RuntimeException("Loan not found with id: " + id);
        }
        return loan;
    }

    @Override
    public Loan createLoan(Loan loan) {
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        loan.setNextPaymentDate(loan.getStartDate().plusMonths(1));
        return loanRepository.save(loan);
    }

    @Override
    public Loan updateLoan(Long id, Loan loan) {
        Loan existing = getLoanById(id);
        existing.setPrincipalAmount(loan.getPrincipalAmount());
        existing.setInterestRate(loan.getInterestRate());
        existing.setStartDate(loan.getStartDate());
        existing.setEndDate(loan.getEndDate());
        return loanRepository.save(existing);
    }

    @Override
    public void deleteLoan(Long id) {
        Loan loan = getLoanById(id);
        loanRepository.delete(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingAmount() {
        List<Loan> allLoans = loanRepository.findAll();
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        for (Loan loan : allLoans) {
            BigDecimal loanAmount = loan.getPrincipalAmount();
            BigDecimal paidAmount = BigDecimal.ZERO;

            for (Payment payment : loan.getPayments()) {
                paidAmount = paidAmount.add(payment.getAmount());
            }

            BigDecimal outstanding = loanAmount.subtract(paidAmount);
            totalOutstanding = totalOutstanding.add(outstanding);
        }

        return totalOutstanding;
    }

    @Override
    public BigDecimal calculateEMI(Long loanId) {
        Loan loan = getLoanById(loanId);
        return loan.calculateEMI();
    }

    @Override
    public void processPayment(Long loanId, BigDecimal amount) {
        Loan loan = getLoanById(loanId);
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setLoan(loan);
        loan.addPayment(payment);
        loanRepository.save(loan);
    }
}
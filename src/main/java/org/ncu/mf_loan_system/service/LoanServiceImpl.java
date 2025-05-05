package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
    }

    @Override
    public Loan createLoan(Loan loan) {
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
}
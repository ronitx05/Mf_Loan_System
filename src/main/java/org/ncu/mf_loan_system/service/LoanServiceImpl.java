package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.exception.InvalidLoanException;
import org.ncu.mf_loan_system.exception.ResourceNotFoundException;
import org.ncu.mf_loan_system.repository.ClientRepository;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;

    public LoanServiceImpl(LoanRepository loanRepository, ClientRepository clientRepository) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Loan createLoan(Loan loan) {
        // Validate client exists
        Long clientId = loan.getClient().getId();
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        // Set default next payment date (1 month from start)
        loan.setNextPaymentDate(loan.getStartDate().plusMonths(1));

        // Validate end date is after start date
        if (loan.getEndDate().isBefore(loan.getStartDate())) {
            throw new InvalidLoanException("End date must be after start date");
        }

        return loanRepository.save(loan);
    }

    @Override
    public BigDecimal getTotalOutstandingAmount() {
        return loanRepository.findAll().stream()
                .map(this::calculateOutstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateOutstanding(Loan loan) {
        BigDecimal totalPaid = loan.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return loan.getPrincipalAmount().subtract(totalPaid);
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


}
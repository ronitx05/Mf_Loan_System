package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Client;
import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.repository.ClientRepository;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    @Override
    public Loan createLoan(Loan loan) {
        Long clientId = loan.getClient().getId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.addLoan(loan);
        clientRepository.save(client);
        return loan;
    }

    @Override
    public Loan updateLoan(Long id, Loan updatedLoan) {
        Loan existingLoan = getLoanById(id);
        existingLoan.setPrincipalAmount(updatedLoan.getPrincipalAmount());
        existingLoan.setInterestRate(updatedLoan.getInterestRate());
        existingLoan.setStartDate(updatedLoan.getStartDate());
        existingLoan.setEndDate(updatedLoan.getEndDate());
        return loanRepository.save(existingLoan);
    }

    @Override
    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalOutstandingAmount() {
        return loanRepository.findAll().stream()
                .map(loan -> loan.getPrincipalAmount().subtract(getTotalPaidAmount(loan)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalPaidAmount(Loan loan) {
        return loan.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateEMI(Long loanId) {
        Loan loan = getLoanById(loanId);
        return loan.calculateEMI(); // Delegate to entity's method
    }

    @Override
    public void processPayment(Long loanId, BigDecimal amount) {
        Loan loan = getLoanById(loanId);
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        loan.addPayment(payment);
        loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoansByClientId(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return client.getLoans();
    }
}
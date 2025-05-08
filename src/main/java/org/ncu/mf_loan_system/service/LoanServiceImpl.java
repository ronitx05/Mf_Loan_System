package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Client;
import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.exception.*;
import org.ncu.mf_loan_system.repository.ClientRepository;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;

    public LoanServiceImpl(LoanRepository loanRepository, ClientRepository clientRepository) {
        this.loanRepository = loanRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        logger.info("Fetching all loans");
        return loanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(Long id) {
        logger.info("Fetching loan with id: {}", id);
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + id));
    }

    @Override
    public Loan createLoan(Loan loan) {
        logger.info("Creating new loan for client: {}", loan.getClient().getId());

        if (loan.getEndDate().isBefore(loan.getStartDate())) {
            throw new InvalidLoanParametersException("End date must be after start date");
        }

        Client client = clientRepository.findById(loan.getClient().getId())
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + loan.getClient().getId()));

        return loanRepository.save(loan);
    }

    @Override
    public Loan updateLoan(Long id, Loan updatedLoan) {
        logger.info("Updating loan with id: {}", id);

        Loan existingLoan = getLoanById(id);
        existingLoan.setPrincipalAmount(updatedLoan.getPrincipalAmount());
        existingLoan.setInterestRate(updatedLoan.getInterestRate());
        existingLoan.setStartDate(updatedLoan.getStartDate());
        existingLoan.setEndDate(updatedLoan.getEndDate());

        return loanRepository.save(existingLoan);
    }

    @Override
    public void deleteLoan(Long id) {
        logger.info("Deleting loan with id: {}", id);
        if (!loanRepository.existsById(id)) {
            throw new LoanNotFoundException("Loan not found with id: " + id);
        }
        loanRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingAmount() {
        logger.info("Calculating total outstanding amount");
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
    @Transactional(readOnly = true)
    public BigDecimal calculateEMI(Long loanId) {
        logger.info("Calculating EMI for loan id: {}", loanId);
        Loan loan = getLoanById(loanId);
        return loan.calculateEMI();
    }

    @Override
    public void processPayment(Long loanId, BigDecimal amount) {
        logger.info("Processing payment of {} for loan id: {}", amount, loanId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException("Payment amount must be positive");
        }

        Loan loan = getLoanById(loanId);

        if (loan.getStatus() == Loan.LoanStatus.PAID) {
            throw new LoanAlreadyPaidException("Cannot process payment - loan is already paid");
        }

        loan.processPayment(amount);
        loanRepository.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOutstandingBalance(Long loanId) {
        logger.info("Getting outstanding balance for loan id: {}", loanId);
        return loanRepository.findById(loanId)
                .map(Loan::getOutstandingAmount)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + loanId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getOverdueLoans() {
        logger.info("Fetching all overdue loans");
        return loanRepository.findByStatusAndEndDateBefore(
                Loan.LoanStatus.ACTIVE, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getLoansByClientId(Long clientId) {
        logger.info("Fetching loans for client id: {}", clientId);
        if (!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException("Client not found with id: " + clientId);
        }
        return loanRepository.findByClientId(clientId);
    }
}
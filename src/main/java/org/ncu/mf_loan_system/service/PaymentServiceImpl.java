package org.ncu.mf_loan_system.service;

import org.ncu.mf_loan_system.entities.Loan;
import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.repository.LoanRepository;
import org.ncu.mf_loan_system.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Override
    public Payment createPayment(Payment payment) {
        // Find the loan from DB
        Loan loan = loanRepository.findById(payment.getLoan().getId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + payment.getLoan().getId()));

        // Save the payment
        payment.setLoan(loan);
        Payment savedPayment = paymentRepository.save(payment);

        // Add payment to loan's payment list
        loan.getPayments().add(savedPayment);

        // Calculate total paid so far
        BigDecimal totalPaid = loan.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total due (principal + interest)
        BigDecimal interestAmount = loan.getPrincipalAmount()
                .multiply(loan.getInterestRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalDue = loan.getPrincipalAmount().add(interestAmount);

        // If fully paid, update status
        if (totalPaid.compareTo(totalDue) >= 0) {
            loan.setStatus(Loan.LoanStatus.PAID);
            loanRepository.save(loan); // update loan status
        }

        return savedPayment;
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        Payment existing = getPaymentById(id);
        existing.setAmount(payment.getAmount());
        existing.setPaymentDate(payment.getPaymentDate());
        return paymentRepository.save(existing);
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
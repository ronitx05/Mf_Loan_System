package org.ncu.mf_loan_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Positive(message = "Principal amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Amount must have up to 2 decimal places")
    private BigDecimal principalAmount;

    @Column(nullable = false)
    @Positive(message = "Interest rate must be positive")
    @DecimalMax(value = "100.0", message = "Interest rate cannot exceed 100%")
    private BigDecimal interestRate;

    @Column(nullable = false)
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Column(nullable = false)
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @Column
    private LocalDate nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "Client is required")
    private Client client;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public enum LoanStatus { ACTIVE, PAID, OVERDUE, DEFAULTED }

    public Loan() {}

    public BigDecimal calculateEMI() {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyRate = interestRate.divide(
                BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        double termMonths = startDate.until(endDate).toTotalMonths();
        BigDecimal factor = monthlyRate.add(BigDecimal.ONE).pow((int) termMonths);
        return principalAmount.multiply(monthlyRate)
                .multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setLoan(this);
        this.nextPaymentDate = payment.getPaymentDate().plusMonths(1);

        BigDecimal totalPaid = BigDecimal.ZERO;
        for (Payment p : payments) {
            totalPaid = totalPaid.add(p.getAmount());
        }

        if (principalAmount.compareTo(totalPaid) <= 0) {
            status = LoanStatus.PAID;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public LocalDate getNextPaymentDate() { return nextPaymentDate; }
    public void setNextPaymentDate(LocalDate nextPaymentDate) { this.nextPaymentDate = nextPaymentDate; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<Payment> getPayments() { return payments; }
}
package org.ncu.mf_loan_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Positive(message = "Principal amount must be positive")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    @Positive(message = "Interest rate must be positive")
    @DecimalMax(value = "100.0")
    private BigDecimal interestRate;

    @Column(nullable = false)
    @PastOrPresent
    private LocalDate startDate;

    @Column(nullable = false)
    @Future
    private LocalDate endDate;

    @Column
    private LocalDate nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public enum LoanStatus { ACTIVE, PAID, OVERDUE, DEFAULTED }

    @PrePersist
    @PreUpdate
    private void calculateInitialValues() {
        if (this.startDate != null && this.endDate != null && this.nextPaymentDate == null) {
            this.nextPaymentDate = this.startDate.plusMonths(1);
        }
    }

    public BigDecimal calculateEMI() {
        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        if (months <= 0) return principalAmount;

        BigDecimal monthlyRate = interestRate.divide(
                BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow((int) months);
        return principalAmount.multiply(monthlyRate)
                .multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
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
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}
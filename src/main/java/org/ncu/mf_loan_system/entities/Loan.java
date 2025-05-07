package org.ncu.mf_loan_system.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @Future(message = "End date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "Client is required")
    private Client client;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // Required by JPA
    public Loan() {}

    // Constructor for JSON deserialization
    @JsonCreator
    public Loan(
            @JsonProperty("principalAmount") double principalAmount,
            @JsonProperty("interestRate") double interestRate,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate,
            @JsonProperty("client") Client client
    ) {
        this.principalAmount = BigDecimal.valueOf(principalAmount);
        this.interestRate = BigDecimal.valueOf(interestRate);
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.status = LoanStatus.ACTIVE;
        this.nextPaymentDate = startDate != null ? startDate.plusMonths(1) : null;
    }

    // Calculate EMI (Equated Monthly Installment)
    public BigDecimal calculateEMI() {
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100 * 12), 10, BigDecimal.ROUND_HALF_UP);
        double termMonths = startDate.until(endDate).toTotalMonths();
        BigDecimal factor = monthlyRate.add(BigDecimal.ONE).pow((int) termMonths);
        return principalAmount.multiply(monthlyRate)
                .multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP);
    }

    public enum LoanStatus { ACTIVE, PAID, OVERDUE, DEFAULTED }

    // Getters and setters
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

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", nextPaymentDate=" + nextPaymentDate +
                ", status=" + status +
                ", client=" + client +
                ", payments=" + payments +
                '}';
    }
}

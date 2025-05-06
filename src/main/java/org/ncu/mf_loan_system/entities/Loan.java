package org.ncu.mf_loan_system.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    private BigDecimal principalAmount;

    @Column(nullable = false)
    @Positive(message = "Interest rate must be positive")
    private BigDecimal interestRate;

    @Column(nullable = false)
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;  // Renamed for clarity

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // Constructors, getters, setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getNextPaymentDate() {
        return paymentDate;
    }

    public void setNextPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public enum LoanStatus {
        ACTIVE, PAID, OVERDUE, DEFAULTED
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", paymentDate=" + paymentDate +
                ", status=" + status +
                ", client=" + client +
                ", payments=" + payments +
                '}';
    }
}
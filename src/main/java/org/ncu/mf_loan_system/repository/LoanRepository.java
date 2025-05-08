package org.ncu.mf_loan_system.repository;

import org.ncu.mf_loan_system.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatusAndEndDateBefore(Loan.LoanStatus status, LocalDate date);

    @Query("SELECT l FROM Loan l WHERE l.client.id = :clientId")
    List<Loan> findByClientId(Long clientId);
}
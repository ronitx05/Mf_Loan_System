package org.ncu.mf_loan_system.repository;

import org.ncu.mf_loan_system.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan,Long> {

}
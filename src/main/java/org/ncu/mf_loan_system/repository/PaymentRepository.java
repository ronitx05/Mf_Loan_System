package org.ncu.mf_loan_system.repository;

import org.ncu.mf_loan_system.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
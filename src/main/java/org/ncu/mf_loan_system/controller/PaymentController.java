package org.ncu.mf_loan_system.controller;

import org.ncu.mf_loan_system.entities.Payment;
import org.ncu.mf_loan_system.service.PaymentService;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
//@PreAuthorize("isAuthenticated()")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER', 'AUDITOR')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping
//    @PreAuthorize("hasRole('LOAN_OFFICER')")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.status(201).body(paymentService.createPayment(payment));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('LOAN_OFFICER', 'MANAGER')")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.updatePayment(id, payment));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
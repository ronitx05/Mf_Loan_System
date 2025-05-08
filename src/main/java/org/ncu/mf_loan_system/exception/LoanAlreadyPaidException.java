package org.ncu.mf_loan_system.exception;

public class LoanAlreadyPaidException extends RuntimeException {
    public LoanAlreadyPaidException(String message) {
        super(message);
    }
}
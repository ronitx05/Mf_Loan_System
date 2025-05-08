package org.ncu.mf_loan_system.exception;

public class InvalidLoanParametersException extends RuntimeException {
    public InvalidLoanParametersException(String message) {
        super(message);
    }
}
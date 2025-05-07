package org.ncu.mf_loan_system;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.ncu.mf_loan_system.service.LoanServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAndAlertAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggingAndAlertAspect.class);

    // Log when a loan is created
    @AfterReturning(value = "execution(* org.ncu.mf_loan_system.service.LoanService.createLoan(..))")
    public void logAfterLoanCreated(JoinPoint joinPoint) {
        logger.info("New loan issued: " + joinPoint.getArgs()[0]);
    }

    // Log after a payment is recorded
    @AfterReturning(value = "execution(* org.ncu.mf_loan_system.service.PaymentService.createPayment(..))")
    public void logAfterPaymentCreated(JoinPoint joinPoint) {
        logger.info("New payment recorded: " + joinPoint.getArgs()[0]);
    }

    // Log errors
    @AfterThrowing(pointcut = "execution(* org.ncu.mf_loan_system.service..*(..))", throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception in method: " + joinPoint.getSignature().getName(), ex);
    }
}

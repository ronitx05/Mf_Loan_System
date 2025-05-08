package org.ncu.mf_loan_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MfLoanSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MfLoanSystemApplication.class, args);
	}

}

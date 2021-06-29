package com.lulobank.credits.v3.service;

import java.math.BigDecimal;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RiskEngineResultValidationsService {
	
    private static final String CREDIT_APPROVED = "PASS";
	private static final String EVENT_STATUS_COMPLETED = "COMPLETED";
    private static final BigDecimal MIN_LOAN_AMOUNT = new BigDecimal(1000000);
    
    private final CreditsV3Repository creditsV3Repository;
    
    public Either<String, String> isPreApprovedCredit(String approved) {
    	return Option.of(approved)
    			.filter(eventApproved -> eventApproved.equals(CREDIT_APPROVED))
    			.toEither("Loan is not Approved");
    }
    
    public Either<String, BigDecimal> amountIsBiggerThanMinLoanAmount(BigDecimal loanAmount) {
    	return Option.of(loanAmount)
    			.filter(eventLoanAmount -> eventLoanAmount.compareTo(MIN_LOAN_AMOUNT) >= 0)
    			.toEither("Loan amount is under the min value");
    }
    
    public Either<String, String> clientDoesntHaveActiveCredit(String idClient) {
    	return Option.of(idClient)
				.filter(eventIdClient -> creditsV3Repository.findLoanActiveByIdClient(eventIdClient).isEmpty())
				.toEither("Client already has an active loan");
    }
    
    public Either<String, String> isEventCompleted(String status) {
		return Option.of(status)
				.filter(eventStatus -> EVENT_STATUS_COMPLETED.equalsIgnoreCase(eventStatus))
				.toEither("Event is not Completed");
	}
}

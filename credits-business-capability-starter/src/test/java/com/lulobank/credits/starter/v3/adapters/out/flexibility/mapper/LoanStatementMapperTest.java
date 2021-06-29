package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import flexibility.client.enums.LoanAccountPaymentStatus;
import flexibility.client.models.response.GetLoanStatementResponse;
import flexibility.client.models.response.GetLoanStatementResponse.LoanData;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanStatementMapperTest {
	
	private static final Double DOUBLE_VALUE = 0.0D;
	
	@Test
	public void mapper() {
		
		GetLoanStatementResponse getLoanStatementResponse = buildGetLoanStatementResponse();
		
		LoanStatement response = LoanStatementMapper.loanStatementResponse(getLoanStatementResponse,
				LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
		assertEquals(response.getLoanAmount(), String.valueOf(getLoanStatementResponse.getLoanAmount()));
	}

	private GetLoanStatementResponse buildGetLoanStatementResponse() {
		GetLoanStatementResponse getLoanStatementResponse = new GetLoanStatementResponse();
		getLoanStatementResponse.setInstalments(2);
		getLoanStatementResponse.setLoanData(buildLoanData());
		getLoanStatementResponse.setPreviousLoanPeriodData(buildLoanData());
		getLoanStatementResponse.setInArrearsBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setTotalBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setPrincipalPaid(DOUBLE_VALUE);
		getLoanStatementResponse.setLoanAmount(DOUBLE_VALUE);
		getLoanStatementResponse.setDisbursementDate(LocalDateTime.now());
		getLoanStatementResponse.setPenaltyRate(DOUBLE_VALUE);
		getLoanStatementResponse.setAmortization("");
		getLoanStatementResponse.setAccruedPenalty(DOUBLE_VALUE);
		getLoanStatementResponse.setPenaltyBalance(DOUBLE_VALUE);
		getLoanStatementResponse.setDaysInArrears(5L);
		getLoanStatementResponse.setState(LoanAccountPaymentStatus.IN_ARREARS);
		getLoanStatementResponse.setInterestRate(DOUBLE_VALUE);
		return getLoanStatementResponse;
	}

	private LoanData buildLoanData() {
		LoanData loanData = new LoanData();
		loanData.setCutOffDate(LocalDate.now());
		loanData.setInstalmentDueDate(LocalDate.now());
		loanData.setInstalmentTotalDue(DOUBLE_VALUE);
		loanData.setInstalmentPrincipalDue(DOUBLE_VALUE);
		loanData.setInstalmentInterestDue(DOUBLE_VALUE);
		loanData.setInstalmentPenaltiesDue(DOUBLE_VALUE);
		loanData.setFeesAmount(DOUBLE_VALUE);
		loanData.setLegalExpenses(DOUBLE_VALUE);
		loanData.setInstalment(3);
		loanData.setTotalPaid(DOUBLE_VALUE);
		loanData.setPrincipalPaid(DOUBLE_VALUE);
		loanData.setInterestPaid(DOUBLE_VALUE);
		loanData.setPenaltyPaid(DOUBLE_VALUE);
		loanData.setFeesPaid(DOUBLE_VALUE);
		return loanData;
	}

}

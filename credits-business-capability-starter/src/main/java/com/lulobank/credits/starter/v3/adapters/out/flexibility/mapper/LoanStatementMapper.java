package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.LoanStatement;
import flexibility.client.models.response.GetLoanStatementResponse;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static com.lulobank.credits.services.utils.InterestUtil.dailyPeriodicRateToAnnual;
import static com.lulobank.credits.services.utils.InterestUtil.getAnnualEffectiveRateFromAnnualNominalRate;

public class LoanStatementMapper {
	
	private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter LOCAL_TIME_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss");
	
	private LoanStatementMapper() {}
	
	public static LoanStatement loanStatementResponse(GetLoanStatementResponse getLoanStatementResponse, String statementDate) {
		return LoanStatement.builder()
				.totalInstalments(getLoanStatementResponse.getInstalments().toString())
				.cutOffDate(getLoanStatementResponse.getLoanData().getCutOffDate().format(LOCAL_DATE_FORMATTER))
				.instalmentDueDate(getLoanStatementResponse.getLoanData().getInstalmentDueDate().format(LOCAL_DATE_FORMATTER))
				.instalmentTotalDue(String.valueOf(getLoanStatementResponse.getLoanData().getInstalmentTotalDue() +
						getLoanStatementResponse.getLoanData().getTotalPaid()))
				.instalmentPrincipalDue(String.valueOf(getLoanStatementResponse.getLoanData()
						.getInstalmentPrincipalDue() + getLoanStatementResponse.getLoanData().getPrincipalPaid()))
				.instalmentInterestDue(String.valueOf(getLoanStatementResponse.getLoanData()
						.getInstalmentInterestDue() + getLoanStatementResponse.getLoanData().getInterestPaid()))
				.instalmentPenaltiesDue(getLoanStatementResponse.getPenaltyBalance().toString())
				.inArrearsBalance(String.valueOf(getLoanStatementResponse.getInArrearsBalance() -
						getLoanStatementResponse.getPenaltyBalance()))
				.insuranceFee(String.valueOf(getLoanStatementResponse.getLoanData().getFeesAmount() +
						getLoanStatementResponse.getLoanData().getFeesPaid()))
				.legalExpenses(getLoanStatementResponse.getLoanData().getLegalExpenses().toString())
				.currentInstalment(getLoanStatementResponse.getLoanData().getInstalment().toString())
				.lastPeriodTotalPaid(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getTotalPaid().toString())
						.getOrNull())
				.lastPeriodPrincipalPaid(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getPrincipalPaid().toString())
						.getOrNull())
				.lastPeriodInterestPaid(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getInterestPaid().toString())
						.getOrNull())
				.lastPeriodPenaltyPaid(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getPenaltyPaid().toString())
						.getOrNull())
				.lastPeriodInsuranceFee(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getFeesPaid().toString())
						.getOrNull())
				.lastPeriodLegalExpenses(Option.of(getLoanStatementResponse.getPreviousLoanPeriodData())
						.map(previousLoanPeriodData -> previousLoanPeriodData.getLegalExpenses().toString())
						.getOrNull())
				.totalBalance(getLoanStatementResponse.getTotalBalance().toString())
				.principalPaid(getLoanStatementResponse.getPrincipalPaid().toString())
				.loanAmount(getLoanStatementResponse.getLoanAmount().toString())
				.disbursementDate(getLoanStatementResponse.getDisbursementDate().format(LOCAL_TIME_DATE_FORMATTER))
				.interestRate(getAnnualEffectiveRateFromAnnualNominalRate(
						BigDecimal.valueOf(getLoanStatementResponse.getInterestRate())).toString())
				.penaltyRate(getAnnualEffectiveRateFromAnnualNominalRate(
						dailyPeriodicRateToAnnual(BigDecimal.valueOf(getLoanStatementResponse.getPenaltyRate()))).toString())
				.amortization(getLoanStatementResponse.getAmortization())
				.daysInArrears(getLoanStatementResponse.getDaysInArrears().toString())
				.loanState(getLoanStatementResponse.getState().getValue())
				.statementDate(statementDate)
				.build();
	}
}

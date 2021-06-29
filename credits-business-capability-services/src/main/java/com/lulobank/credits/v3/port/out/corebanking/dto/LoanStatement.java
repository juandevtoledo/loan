package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoanStatement {
	private String totalInstalments;
	private String cutOffDate;
	private String instalmentDueDate;
	private String instalmentTotalDue;
	private String instalmentPrincipalDue;
	private String instalmentInterestDue;
	private String instalmentPenaltiesDue;
	private String inArrearsBalance;
	private String insuranceFee;
	private String legalExpenses;
	private String currentInstalment;
	private String lastPeriodTotalPaid;
	private String lastPeriodPrincipalPaid;
	private String lastPeriodInterestPaid;
	private String lastPeriodPenaltyPaid;
	private String lastPeriodInsuranceFee;
	private String lastPeriodLegalExpenses;
	private String totalBalance;
	private String principalPaid;
	private String loanAmount;
	private String disbursementDate;
	private String interestRate;
	private String penaltyRate;
	private String amortization;
	private String daysInArrears;
	private String loanState;
	private String statementDate;
}

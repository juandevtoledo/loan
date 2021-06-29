package com.lulobank.credits.v3.port.in.rescheduledloan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RescheduledLoanMessage {
	private String originalLoanId;
	private RescheduledLoan rescheduledLoan;
	private String modificationType;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RescheduledLoan {
		private String id;
		private String creationDate;
		private String lastModifiedDate;
		private String accountState;
		private String productTypeKey;
		private String label;
		private String settlementAccountKey;
		private BigDecimal interestRate;
		private Amount balance;
		private BigDecimal interestBalance;
		private BigDecimal feeBalance;
		private BigDecimal accruedInterest;
		private BigDecimal accruedPenalty;
		private BigDecimal penaltyBalance;
		private Amount loanAmount;
		private String disbursementDate;
		private Amount totalBalance;
		private BigDecimal penaltyRate;
		private Integer daysInArrears;
		private BigDecimal principalPaid;
		private BigDecimal interestPaid;
		private BigDecimal interestFromArrearsPaid;
		private BigDecimal feesPaid;
		private BigDecimal penaltyPaid;
		private BigDecimal totalPaid;
		private String statusLoan;
		private Integer paymentDay;
		private String localCreationDate;
		private String clientId;
		private String dateOfPay;
		private Integer installments;
	}

	@Getter
	@Setter
	public static class Amount {
		private String currency;
		private BigDecimal amount;
	}
}

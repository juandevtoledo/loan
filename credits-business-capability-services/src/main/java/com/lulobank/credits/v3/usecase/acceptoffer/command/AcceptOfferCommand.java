package com.lulobank.credits.v3.usecase.acceptoffer.command;

import com.lulobank.credits.v3.vo.AdapterCredentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcceptOfferCommand {
	private final String idClient;
	private final String idCredit;
	private final String idOffer;
	private final String idProductOffer;
	private final boolean automaticDebitPayments;
	private final String confirmationLoanOTP;
	private final Integer dayOfPay;
	private final AdapterCredentials credentials;
	private final Integer installment;
	private final String loanPurpose;
}

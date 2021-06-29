package com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcceptOfferRequest {

	@NotBlank(message = "IdCredit is null or empty")
	private String idCredit;
	@NotBlank(message = "idProductOffer is null or empty")
	private String idProductOffer;
	@NotBlank(message = "confirmationLoanOTP is null or empty")
	private String confirmationLoanOTP;
	@NotNull(message = "installment is null or empty")
	private Integer installment;
	@NotBlank(message = "idOffer is null or empty")
	private String idOffer;
	private boolean automaticDebitPayments;
	private Integer dayOfPay;
	private String loanPurpose;
	
}

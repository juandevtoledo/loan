package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovedLoanDocument {

	private ClientInfo clientInfo;
	private String acceptOfferDateTime;
	private double requestedAmount;
	private double approvedAmount;
	private int installments;
	private float interestRate;
	private int automaticDebit;
	private int paymentDay;
	private DecevalInfo decevalInformation;

	@Getter
	@Setter
	public static class ClientInfo {
		private String idClient;
		private String idCard;
		private String name;
		private String lastName;
	}

	@Getter
	@Setter
	public static class DecevalInfo {
		private String clientAccountId;
		private String confirmationLoanOTP;
		private String decevalId;
		private String promissoryNoteId;
	}
}

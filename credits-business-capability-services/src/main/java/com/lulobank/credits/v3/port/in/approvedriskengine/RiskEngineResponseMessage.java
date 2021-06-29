package com.lulobank.credits.v3.port.in.approvedriskengine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiskEngineResponseMessage {
	private String idClient;
	private String idProductOffer;
	private RiskEngineAnalysis riskEngineAnalysis;
	private ClientInformation clientInformation;

	@Getter
	@Setter
	public static class RiskEngineAnalysis {
		private double amount;
		private float interestRate;
		private int installments;
		private double maxAmountInstallment;
	}

	@Getter
	@Setter
	public static class ClientInformation {
		private Document documentId;
		private String name;
		private String lastName;
		private String middleName;
		private String secondSurname;
		private String gender;
		private String email;
		private Phone phone;
	}

	@Getter
	@Setter
	public static class Document {
		private String id;
		private String type;
		private String issueDate;
	}

	@Getter
	@Setter
	public static class Phone {
		private String number;
		private String prefix;
	}
}

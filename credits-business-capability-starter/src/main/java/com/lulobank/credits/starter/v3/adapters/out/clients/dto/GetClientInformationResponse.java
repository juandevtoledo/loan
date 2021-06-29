package com.lulobank.credits.starter.v3.adapters.out.clients.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetClientInformationResponse {
	
	private Content content;
	
	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Content {
		private String idClient;
		private String idCard;
		private String name;
		private String lastName;
		private String address;
		private Integer phonePrefix;
		private String phoneNumber;
		private String emailAddress;
		private String idCbs;
		private String idCbsHash;
		private OnBoardingStatus onBoardingStatus;
		private String gender;
		private String documentIssuedBy;
		private String typeDocument;
		private String expirationDate;
		private String expeditionDate;
		private LocalDate birthDate;
		private String capitalizedName;
		private String initialsName;
		private AdditionalPersonalInfo additionalPersonalInfo;
	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AdditionalPersonalInfo {
		private String firstName;
		private String secondName;
		private String firstSurname;
		private String secondSurname;
	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class OnBoardingStatus {
		private String checkpoint;
		private String productSelected;
		private LoanClientRequested loanClientRequested;
	}

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class LoanClientRequested {
		private Double amount;
		private String loanPurpose;
	}
}

package com.lulobank.credits.v3.port.out.clients.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientInformationResponse {
	
	private String idClient;
	private String documentNumber;
	private String expeditionDate;
	private String documentType;
	private String birthDate;
	private String name;
	private String lastName;
	private String gender;
	private String email;
	private Phone phone;
	private ClientAdditionalPersonalInfo clientAdditionalPersonalInfo;
	
	
	@Getter
	@Builder
	public static class Phone {
		private String number;
		private Integer prefix;
	}
	
	@Getter
	@Builder
	public static class ClientAdditionalPersonalInfo {
		private String firstName;
		private String secondName;
		private String firstSurname;
		private String secondSurname;
	}
}

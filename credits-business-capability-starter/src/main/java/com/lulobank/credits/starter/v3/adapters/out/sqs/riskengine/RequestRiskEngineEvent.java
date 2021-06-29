package com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestRiskEngineEvent {
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
	
	@Getter
	@Builder
	public static class Phone {
		private String number;
		private Integer prefix;
	}

}

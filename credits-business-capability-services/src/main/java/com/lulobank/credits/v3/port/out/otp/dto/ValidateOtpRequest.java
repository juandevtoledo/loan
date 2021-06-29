package com.lulobank.credits.v3.port.out.otp.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateOtpRequest {
	private final String idCredit;
	private final String otp;
	private final String idOffer;
	private final String idClient;
	private final Map<String,String> auth;
}

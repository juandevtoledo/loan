package com.lulobank.credits.v3.port.out.otp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateOtpResponse {
	private boolean isValid;
}

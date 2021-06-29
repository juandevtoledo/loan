package com.lulobank.credits.v3.port.out.promissorynote.dto;

import com.lulobank.credits.v3.vo.AdapterCredentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromissoryNoteAsyncServiceRequest {
	private final AdapterCredentials credentials;
	private final String confirmationLoanOTP;
}

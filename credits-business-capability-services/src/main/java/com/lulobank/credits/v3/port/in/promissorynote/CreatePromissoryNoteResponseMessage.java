package com.lulobank.credits.v3.port.in.promissorynote;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePromissoryNoteResponseMessage {
	private String idClient;
	private String idCredit;
	private Map<String, String> headers;
	private Map<String, Object> headersToSQS;
	private String confirmationLoanOTP;
	private String signPassword;
	private Integer clientAccountId;
	private Integer promissoryNoteId;
	private String idCbs;
	private String accountId;

}

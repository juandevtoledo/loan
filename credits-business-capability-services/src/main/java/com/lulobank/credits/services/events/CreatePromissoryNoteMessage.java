package com.lulobank.credits.services.events;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePromissoryNoteMessage {

	private String name;
	private String lastName;
	private String middleName;
	private String secondSurname;
	private String email;
	private DocumentId documentId;
	private String idClient;
	private String idCredit;
	private String confirmationLoanOTP;
	private Map<String, String> headers;
	private Map<String, Object> headersToSQS;
	private String idCbs;
	private String accountId;


	@Getter
	@Setter
	public static class DocumentId {
		private String id;
		private String type;
		private String issueDate;
		private String expirationDate;
	}
}

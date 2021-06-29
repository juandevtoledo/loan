package com.lulobank.credits.services.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSignedDocumentGroupEvent {
	private String emailAddress;
	private String idClient;
	private String idSignedDocumentGroup;
}

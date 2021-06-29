package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.SendSignedDocumentGroupEvent;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;

public class SendSignedDocumentGroupSqs extends SqsV3Integration<LoanTransaction, SendSignedDocumentGroupEvent> {

	private static final String ID_SIGNED_DOCUMENT_GROUP = "CREDIT_DOCUMENTS";

	public SendSignedDocumentGroupSqs(String endpoint) {
		super(endpoint);
	}

	@Override
	public Event<SendSignedDocumentGroupEvent> map(LoanTransaction event) {
		SendSignedDocumentGroupEvent sendSignedDocumentGroupEvent = new SendSignedDocumentGroupEvent();
		sendSignedDocumentGroupEvent.setEmailAddress(event.getEntity().getClientInformation().getEmail());
		sendSignedDocumentGroupEvent.setIdClient(event.getEntity().getIdClient());
		sendSignedDocumentGroupEvent.setIdSignedDocumentGroup(ID_SIGNED_DOCUMENT_GROUP);
		
		return EventFactory.ofDefaults(sendSignedDocumentGroupEvent).build();
	}

}

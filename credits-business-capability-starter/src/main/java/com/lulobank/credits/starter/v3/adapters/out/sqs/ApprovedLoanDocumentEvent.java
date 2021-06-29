package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.ApprovedLoanDocument;
import com.lulobank.credits.starter.v3.mappers.ApprovedLoanDocumentMapper;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;

public class ApprovedLoanDocumentEvent extends SqsV3Integration<LoanTransaction, ApprovedLoanDocument> {

	public ApprovedLoanDocumentEvent(String endpoint) {
		super(endpoint);
	}

	@Override
	public Event<ApprovedLoanDocument> map(LoanTransaction loanTransaction) {
		return EventFactory
				.ofDefaults(ApprovedLoanDocumentMapper.INSTANCE.loanTransactionToApprovedLoanDocument(loanTransaction))
				.build();
	}
}

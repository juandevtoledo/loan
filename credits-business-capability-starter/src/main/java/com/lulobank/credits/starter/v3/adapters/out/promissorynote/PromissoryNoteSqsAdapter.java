package com.lulobank.credits.starter.v3.adapters.out.promissorynote;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.mappers.CreatePromissoryNoteEventMapper;
import com.lulobank.credits.v3.port.out.promissorynote.PromissoryNoteAsyncService;
import com.lulobank.credits.v3.port.out.promissorynote.dto.PromissoryNoteAsyncServiceRequest;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.events.api.EventFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PromissoryNoteSqsAdapter implements PromissoryNoteAsyncService {

	private final SqsBraveTemplate sqsBraveTemplate;
	private final String promissoryNoteQueue;
	private static final int DELAY = 300;

	@Override
	public void createPromissoryNote(LoanTransaction event, AcceptOffer acceptOffer) {
		
		sqsBraveTemplate.convertAndSend(promissoryNoteQueue, EventFactory
				.ofDefaults(CreatePromissoryNoteEventMapper.INSTANCE.loanTransactionToCreatePromissoryNoteMessage(event, event.getEntity().getIdCredit().toString(), acceptOffer))
				.delay(DELAY)
				.build());
	}
	
	@Override
	public void createPromissoryNote(LoanTransaction event, PromissoryNoteAsyncServiceRequest promissoryNoteAsyncServiceReques) {
		sqsBraveTemplate.convertAndSend(promissoryNoteQueue, EventFactory
				.ofDefaults(CreatePromissoryNoteEventMapper.INSTANCE.loanTransactionToCreatePromissoryNoteMessage(event, event.getEntity().getIdCredit().toString(), promissoryNoteAsyncServiceReques))
				.delay(DELAY)
				.build());
	}
}

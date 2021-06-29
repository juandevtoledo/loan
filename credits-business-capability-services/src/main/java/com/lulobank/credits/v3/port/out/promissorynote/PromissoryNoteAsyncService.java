package com.lulobank.credits.v3.port.out.promissorynote;

import com.lulobank.credits.v3.port.out.promissorynote.dto.PromissoryNoteAsyncServiceRequest;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;

public interface PromissoryNoteAsyncService {
	
	void createPromissoryNote(LoanTransaction event, AcceptOffer acceptOffer);

	void createPromissoryNote(LoanTransaction event, PromissoryNoteAsyncServiceRequest promissoryNoteAsyncServiceRequest);
}

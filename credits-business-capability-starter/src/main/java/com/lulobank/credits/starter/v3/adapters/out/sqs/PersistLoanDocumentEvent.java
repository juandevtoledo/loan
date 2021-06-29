package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.PersistLoanDocument;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Option;

import static org.apache.logging.log4j.util.Strings.EMPTY;

public class PersistLoanDocumentEvent extends SqsV3Integration<LoanTransaction, PersistLoanDocument> {

    public PersistLoanDocumentEvent(String endpoint) {
        super(endpoint);
    }

    @Override
    public Event<PersistLoanDocument> map(LoanTransaction loanTransaction) {
        PersistLoanDocument persistLoanDocument = new PersistLoanDocument();
        persistLoanDocument.setIdClient(loanTransaction.getEntity().getIdClient());
        persistLoanDocument.setIdCredit(loanTransaction.getEntity().getIdCredit().toString());
        persistLoanDocument.setIdCbs(loanTransaction.getSavingsAccountResponse().getIdCbs());
        persistLoanDocument.setIdCard(getIdCard(loanTransaction));
        persistLoanDocument.setAccountId(loanTransaction.getSavingsAccountResponse().getAccountId());
        return EventFactory.ofDefaults(persistLoanDocument).build();
    }

    private String getIdCard(LoanTransaction loanTransaction) {
        return Option.of(loanTransaction.getEntity().getClientInformation())
                .map(clientInformationV3 -> clientInformationV3.getDocumentId().getId())
                .getOrElse(EMPTY);
    }
}

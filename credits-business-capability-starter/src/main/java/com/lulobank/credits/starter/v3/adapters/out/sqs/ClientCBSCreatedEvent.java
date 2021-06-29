package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.CBSCreated;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;

public class ClientCBSCreatedEvent extends SqsV3Integration<LoanTransaction, CBSCreated> {

    public ClientCBSCreatedEvent(String endpoint) {
        super(endpoint);
    }

    @Override
    public Event<CBSCreated> map(LoanTransaction event) {

        CBSCreated cbsCreated = new CBSCreated();
        cbsCreated.setIdCbs(event.getSavingsAccountResponse().getIdCbs());
        cbsCreated.setIdCbsHash(event.getEntity().getEncodedKeyClientMambu());
        cbsCreated.setIdClient(event.getEntity().getIdClient());
        return EventFactory.ofDefaults(cbsCreated).build();
    }
}

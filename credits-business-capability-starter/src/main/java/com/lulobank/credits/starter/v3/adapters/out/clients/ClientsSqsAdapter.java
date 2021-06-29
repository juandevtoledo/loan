package com.lulobank.credits.starter.v3.adapters.out.clients;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.services.events.CreditAccepted;
import com.lulobank.credits.v3.port.out.ClientsAsyncService;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.events.api.EventFactory;
import lombok.AllArgsConstructor;

import static com.lulobank.clients.sdk.operations.util.CheckPoints.LOAN_CREATED;

@AllArgsConstructor
public class ClientsSqsAdapter implements ClientsAsyncService {

    private final SqsBraveTemplate sqsBraveTemplate;
    private final String clientsQueue;

    @Override
    public void updateSavingAccountCheckPoint(CreditsV3Entity creditsV3Entity) {
        sqsBraveTemplate.convertAndSend(clientsQueue, EventFactory
                .ofDefaults(new CreditAccepted(creditsV3Entity.getIdClient(), LOAN_CREATED.name()))
                .build());
    }
}

package com.lulobank.credits.starter.v3.adapters.out.clientalerts;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.mappers.CreditFinishedNotificationMapper;
import com.lulobank.credits.v3.port.out.ClientAlertsAsyncService;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.EventFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientAlertsSqsAdapter implements ClientAlertsAsyncService {

    private final SqsBraveTemplate sqsBraveTemplate;
    private final String clientAlertsQueue;

    @Override
    public void sendCreditFinishedNotification(LoanTransaction loanTransaction) {
        sqsBraveTemplate.convertAndSend(clientAlertsQueue, EventFactory
                .ofDefaults(CreditFinishedNotificationMapper.INSTANCE.
                        creditsV3EntityToNewNotificationEvent(loanTransaction.getEntity()))
                .build());
    }
}

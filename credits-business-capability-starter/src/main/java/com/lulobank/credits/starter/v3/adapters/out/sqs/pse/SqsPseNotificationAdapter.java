package com.lulobank.credits.starter.v3.adapters.out.sqs.pse;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.sqs.pse.dto.LoanClosedByPSETotalPaymentMessage;
import com.lulobank.credits.v3.port.out.queue.PseAsyncService;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SqsPseNotificationAdapter implements PseAsyncService {

    private final String pseSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> loanClosed(String idClient, String productType) {
        return Try.run(()->sqsBraveTemplate.convertAndSend(pseSqsEndpoint ,
                EventFactory
                .ofDefaults(new LoanClosedByPSETotalPaymentMessage(idClient, productType))
                .build()));
    }
}

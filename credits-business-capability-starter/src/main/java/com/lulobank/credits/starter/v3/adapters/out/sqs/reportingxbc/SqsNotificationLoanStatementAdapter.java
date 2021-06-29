package com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.events.CreateStatementMessage;
import com.lulobank.credits.v3.port.out.queue.NotificationLoanStatement;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;

public class SqsNotificationLoanStatementAdapter implements NotificationLoanStatement {

    private final String reportsSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;
    private final Integer maximumReceives;
    private final Integer delay;

    public SqsNotificationLoanStatementAdapter(String reportsSqsEndpoint, SqsBraveTemplate sqsBraveTemplate, Integer maximumReceives, Integer delay) {
        this.reportsSqsEndpoint = reportsSqsEndpoint;
        this.sqsBraveTemplate = sqsBraveTemplate;
        this.maximumReceives = maximumReceives;
        this.delay = delay;
    }

    @Override
    public Try<Void> requestLoanStatement(CreateStatementMessage createStatementMessage) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(reportsSqsEndpoint,
                EventFactory.ofDefaults(createStatementMessage)
                        .delay(delay)
                        .maximumReceives(maximumReceives)
                        .build()));
    }
}
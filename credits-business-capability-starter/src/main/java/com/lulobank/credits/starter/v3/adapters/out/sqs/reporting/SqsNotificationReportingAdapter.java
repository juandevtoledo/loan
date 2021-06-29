package com.lulobank.credits.starter.v3.adapters.out.sqs.reporting;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import com.lulobank.events.api.EventFactory;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class SqsNotificationReportingAdapter implements ReportingQueueService {
    private final String reportsSqsEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public Try<Void> sendGoodStanding(GoodStandingCertificateEvent goodStandingCertificateEvent) {
        return Try.run(() -> sqsBraveTemplate.convertAndSend(reportsSqsEndpoint,
                EventFactory.ofDefaults(goodStandingCertificateEvent)
                        .build(), new HashMap<>()));
    }
}

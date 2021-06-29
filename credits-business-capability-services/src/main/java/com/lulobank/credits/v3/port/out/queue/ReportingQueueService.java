package com.lulobank.credits.v3.port.out.queue;

import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import io.vavr.control.Try;

public interface ReportingQueueService {

    Try<Void> sendGoodStanding(GoodStandingCertificateEvent goodStandingCertificateEvent);
}

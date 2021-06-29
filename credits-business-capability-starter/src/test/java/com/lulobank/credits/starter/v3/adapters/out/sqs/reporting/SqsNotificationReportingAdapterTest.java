package com.lulobank.credits.starter.v3.adapters.out.sqs.reporting;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.events.GoodStandingCertificateEvent;
import com.lulobank.events.api.Event;
import io.vavr.control.Try;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

public class SqsNotificationReportingAdapterTest {
    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    @Captor
    protected ArgumentCaptor<Event<GoodStandingCertificateEvent>> eventCaptor;
    private SqsNotificationReportingAdapter sqsNotificationReportingAdapter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sqsNotificationReportingAdapter = new SqsNotificationReportingAdapter("http://localhost.fake", sqsBraveTemplate);
    }

    @Test
    public void processEventOk() {
        Mockito.doNothing().when(sqsBraveTemplate).convertAndSend(anyString(), eventCaptor.capture(),anyMap());
        GoodStandingCertificateEvent goodStandingCertificateEvent = goodStandingCertificateEvent();
        Try<Void> response = sqsNotificationReportingAdapter.sendGoodStanding(goodStandingCertificateEvent);
        assertThat(response.isSuccess(), Matchers.is(true));
        assertThat(eventCaptor.getValue().getEventType(), Matchers.is(GoodStandingCertificateEvent.class.getSimpleName()));
        assertThat(eventCaptor.getValue().getPayload().getIdClient(), Matchers.is(goodStandingCertificateEvent.getIdClient()));
    }

    private GoodStandingCertificateEvent goodStandingCertificateEvent() {
        return GoodStandingCertificateEvent.builder()
                .idClient(UUID.randomUUID().toString())
                .amount(BigDecimal.TEN)
                .typeReport("TYPE_REPORT")
                .closedDate(LocalDateTime.now().toString())
                .acceptDate(LocalDateTime.now().toString())
                .idLoanAccountMambu("LOAN_01")
                .build();
    }

}

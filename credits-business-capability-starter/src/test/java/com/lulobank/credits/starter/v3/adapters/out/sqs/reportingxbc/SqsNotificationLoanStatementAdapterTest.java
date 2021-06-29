package com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.starter.v3.adapters.out.sqs.reportingxbc.SqsNotificationLoanStatementAdapter;
import com.lulobank.credits.v3.events.CreateStatementMessage;
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

import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

public class SqsNotificationLoanStatementAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    private SqsNotificationLoanStatementAdapter sqsNotificationLoanStatementAdapter;
    @Captor
    protected ArgumentCaptor<Event<CreateStatementMessage>> eventCaptor;
    private static final Integer delay=10;
    private static final Integer maximumReceives=1;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        sqsNotificationLoanStatementAdapter=new SqsNotificationLoanStatementAdapter("http://localhost.fake",sqsBraveTemplate,maximumReceives,delay);
    }

    @Test
    public void processEventOk(){
        Mockito.doNothing().when(sqsBraveTemplate).convertAndSend(anyString(),eventCaptor.capture());
        CreateStatementMessage createStatementMessage =createLoanStatementBuild();
        Try<Void> response=sqsNotificationLoanStatementAdapter.requestLoanStatement(createStatementMessage);
        assertThat(response.isSuccess(), Matchers.is(true));
        assertThat(eventCaptor.getValue().getDelay(), Matchers.is(delay));
        assertThat(eventCaptor.getValue().getMaximumReceives(), Matchers.is(maximumReceives));
        assertThat(eventCaptor.getValue().getEventType(), Matchers.is(CreateStatementMessage.class.getSimpleName()));
        assertThat(eventCaptor.getValue().getPayload().getIdClient(), Matchers.is(createStatementMessage.getIdClient()));
    }

    private CreateStatementMessage createLoanStatementBuild(){
       return CreateStatementMessage.builder()
                .idClient(UUID.randomUUID().toString())
                .reportType("REPORT_TYPE")
                .productType("CREDITS")
                .build();
    }
} 
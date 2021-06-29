package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.NewReportEvent;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountResponseBuilder;
import static com.lulobank.credits.starter.v3.adapters.out.sqs.TypeReport.CREDIT_CONTRACT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportingV3SqsTest {
    private ReportingV3Sqs testClass;
    private String sqsEndpoint = "http://sqs.endpoint.com";

    @Before
    public void setup() {
        testClass = new ReportingV3Sqs(sqsEndpoint);
    }

    @Test
    public void map() {
        LoanTransaction loanTransaction = loanTransactionBuilder(savingsAccountResponseBuilder());
        Event<NewReportEvent> event = testClass.map(loanTransaction);
        assertThat("Event name is right", event.getEventType(), is(NewReportEvent.class.getSimpleName()));
        assertThat("IdCbs is right", event.getPayload().getTypeReport(), is(CREDIT_CONTRACT));
        assertThat("IdClient is right", event.getPayload().getIdClient(), is(ID_CLIENT));
    }


}

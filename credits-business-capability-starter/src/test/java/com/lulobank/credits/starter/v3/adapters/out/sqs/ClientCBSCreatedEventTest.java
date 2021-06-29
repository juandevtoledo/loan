package com.lulobank.credits.starter.v3.adapters.out.sqs;

import com.lulobank.credits.services.events.CBSCreated;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT_MAMBU;
import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountResponseBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientCBSCreatedEventTest {

    private ClientCBSCreatedEvent testClass;
    private String sqsEndpoint = "http://sqs.endpoint.com";

    @Before
    public void setup() {
        testClass = new ClientCBSCreatedEvent(sqsEndpoint);
    }

    @Test
    public void map() {
        LoanTransaction loanTransaction = loanTransactionBuilder(savingsAccountResponseBuilder());
        Event<CBSCreated> event = testClass.map(loanTransaction);
        assertThat("Event name is right", event.getEventType(), is(CBSCreated.class.getSimpleName()));
        assertThat("Checkpoint is right", event.getPayload().getIdCbs(), is(ID_CLIENT_MAMBU));
        assertThat("IdClient is right", event.getPayload().getIdClient(), is(ID_CLIENT));
    }
}

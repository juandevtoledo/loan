package com.lulobank.credits.starter.v3.adapters.out.sqs;

import static com.lulobank.credits.starter.utils.Constants.ID_CARD;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT_MAMBU;
import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountResponseBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.services.events.PersistLoanDocument;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.events.api.Event;

public class PersistLoanDocumentEventTest {
    private PersistLoanDocumentEvent testClass;
    private String sqsEndpoint = "http://sqs.endpoint.com";
    @Mock
    private
    Consumer<SqsV3Integration<LoanTransaction, PersistLoanDocument>.SqsCommand> consumer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testClass = new PersistLoanDocumentEvent(sqsEndpoint);
    }

    @Test
    public void map() {
        LoanTransaction loanTransaction = loanTransactionBuilder(savingsAccountResponseBuilder());
        Event<PersistLoanDocument> event = testClass.map(loanTransaction);
        assertThat("Event name is right", event.getEventType(), is(PersistLoanDocument.class.getSimpleName()));
        assertThat("Checkpoint is right", event.getPayload().getIdCbs(), is(ID_CLIENT_MAMBU));
        assertThat("IdClient is right", event.getPayload().getIdClient(), is(ID_CLIENT));
        assertThat("IdCard is right", event.getPayload().getIdCard(), is(ID_CARD));
    }

}

package com.lulobank.credits.starter.v3.adapters.out.clients;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.services.events.CreditAccepted;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ClientsSqsAdapterTest {

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    @Captor
    private ArgumentCaptor<Event<CreditAccepted>> eventArgumentCaptor;

    private ClientsSqsAdapter clientsSqsAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientsSqsAdapter = new ClientsSqsAdapter(sqsBraveTemplate, "sqsEndpointClients");
    }

    @Test
    public void shouldSendCreditAcceptedUpdateCheckpoint() {
        CreditsV3Entity creditsV3Entity = new CreditsV3Entity();
        creditsV3Entity.setIdClient(ID_CLIENT);
        clientsSqsAdapter.updateSavingAccountCheckPoint(creditsV3Entity);

        verify(sqsBraveTemplate).convertAndSend(eq("sqsEndpointClients"), eventArgumentCaptor.capture());

        Event<CreditAccepted> event = eventArgumentCaptor.getValue();

        assertThat(event, notNullValue());

        CreditAccepted payload = event.getPayload();
        assertThat(payload, notNullValue());
        assertThat(payload.getCheckpoint(), is("LOAN_CREATED"));
        assertThat(payload.getIdClient(), is(ID_CLIENT));
    }
}
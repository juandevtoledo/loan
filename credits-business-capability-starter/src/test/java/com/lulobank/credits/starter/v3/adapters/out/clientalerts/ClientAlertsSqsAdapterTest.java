package com.lulobank.credits.starter.v3.adapters.out.clientalerts;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.services.events.CreditFinishedNotificationEvent;
import com.lulobank.events.api.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ClientAlertsSqsAdapterTest {

    @Captor
    private ArgumentCaptor<Event<CreditFinishedNotificationEvent>> eventArgumentCaptor;

    @Mock
    private SqsBraveTemplate sqsBraveTemplate;
    private ClientAlertsSqsAdapter clientAlertsSqsAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        clientAlertsSqsAdapter = new ClientAlertsSqsAdapter(sqsBraveTemplate, "clientAlertsQueueTest");
    }

    @Test
    public void shouldSendCreditFinishedNotification() {
        clientAlertsSqsAdapter.sendCreditFinishedNotification(loanTransactionBuilder());

        verify(sqsBraveTemplate).convertAndSend(eq("clientAlertsQueueTest"), eventArgumentCaptor.capture());

        Event<CreditFinishedNotificationEvent> event = eventArgumentCaptor.getValue();

        assertThat(event, notNullValue());

        CreditFinishedNotificationEvent payload = event.getPayload();
        assertThat(payload, notNullValue());
        assertThat(payload.getDescription(), is("El dinero del crédito No. 92377900292 por $ 3.000.000 se ha depositado en tu Lulo Cuenta."));
        assertThat(payload.getTransactionType(), is("CREDIT_FINISHED"));
        assertThat(payload.getInAppNotification(), notNullValue());
        assertThat(payload.getInAppNotification().getDescription(), is("El dinero del crédito No. 92377900292 por $ 3.000.000 se ha depositado en tu Lulo Cuenta."));
        assertThat(payload.getInAppNotification().getTittle(), is("Desembolso exitoso"));
        assertThat(payload.getInAppNotification().getIdClient(), is("37b56ce1-0085-479c-8663-0cdc72802df8"));
        assertThat(payload.getInAppNotification().getAction(), is("OPENED_LOAN"));
    }
}
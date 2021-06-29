package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.starter.v3.adapters.in.sqs.event.CloseLoanByPSETotalPaymentMessage;
import com.lulobank.credits.v3.usecase.closeloan.CloseLoanByExternalPaymentUseCase;
import com.lulobank.credits.v3.usecase.closeloan.command.ClientWithExternalPayment;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class CloseLoanByPSEHandlerTest {

    @Mock
    private CloseLoanByExternalPaymentUseCase closeLoanByExternalPaymentUseCase;
    @Captor
    private ArgumentCaptor<ClientWithExternalPayment> clientWithExternalPaymentCaptor;
    private CloseLoanByPSEHandler closeLoanByPSEHandler;
    private final String idClient = UUID.randomUUID().toString();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        closeLoanByPSEHandler = new CloseLoanByPSEHandler(closeLoanByExternalPaymentUseCase);
    }

    @Test
    public void processMessage_WhenUseCaseResponseSuccess() {
        when(closeLoanByExternalPaymentUseCase.execute(clientWithExternalPaymentCaptor.capture())).thenReturn(Either.right(idClient));
        Try<Void> response = closeLoanByPSEHandler.execute(new CloseLoanByPSETotalPaymentMessage(idClient,"PAYMENT_TOTAL"));
        assertThat(response.isSuccess(), is(true));
        assertThat(clientWithExternalPaymentCaptor.getValue().getIdClient(), is(idClient));
    }

    @Test
    public void processMessage_WhenUseCaseResponseError() {
        when(closeLoanByExternalPaymentUseCase.execute(clientWithExternalPaymentCaptor.capture())).thenReturn(Either.left(CreditsError.databaseError()));
        Try<Void> response = closeLoanByPSEHandler.execute(new CloseLoanByPSETotalPaymentMessage(idClient,"PAYMENT_TOTAL"));
        assertThat(response.isFailure(), is(true));
        assertThat(clientWithExternalPaymentCaptor.getValue().getIdClient(), is(idClient));
    }
}


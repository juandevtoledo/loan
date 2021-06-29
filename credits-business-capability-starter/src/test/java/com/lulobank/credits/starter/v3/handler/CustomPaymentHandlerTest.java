package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.v3.usecase.payment.CustomPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.command.CustomPaymentInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.utils.Constants.*;
import static com.lulobank.credits.starter.utils.Samples.paymentCustomRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

public class CustomPaymentHandlerTest {
    @Mock
    private CustomPaymentUseCase customPaymentUseCase;
    private CustomPaymentHandler customPaymentHandler;
    @Captor
    private ArgumentCaptor<CustomPaymentInstallment> paymentCustomInstallmentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        customPaymentHandler = new CustomPaymentHandler(customPaymentUseCase);
    }

    @Test
    public void makePayment() {
        when(customPaymentUseCase.execute(paymentCustomInstallmentCaptor.capture())).thenReturn(Either.right(true));
        ResponseEntity<AdapterResponse> responseEntity = customPaymentHandler.makePayment(paymentCustomRequest(), ID_CLIENT);
        assertThat(responseEntity.getStatusCode(), is(ACCEPTED));
        assertThat(paymentCustomInstallmentCaptor.getValue().getAmount().doubleValue(), is(AMOUNT));
        assertThat(paymentCustomInstallmentCaptor.getValue().getClientId(), is(ID_CLIENT));
        assertThat(paymentCustomInstallmentCaptor.getValue().getCoreCbsId(), is(ID_LOAN));
    }

    @Test
    public void makePaymentFailedSinceUseCaseError() {

        when(customPaymentUseCase.execute(paymentCustomInstallmentCaptor.capture())).thenReturn(Either.left(new UseCaseResponseError("CRE_106", "502")));
        ResponseEntity<AdapterResponse> responseEntity = customPaymentHandler.makePayment(paymentCustomRequest(), ID_CLIENT);
        assertThat(responseEntity.getStatusCode(), is(NOT_ACCEPTABLE));
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_106"));
        assertThat(errorResponse.getDetail(), is("D"));

    }
}

package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentResponse;
import com.lulobank.credits.v3.usecase.payment.PaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.dto.Payment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.paymentRequest;
import static com.lulobank.credits.starter.v3.util.EntitiesFactory.PaymentFactory.paymentResponse;
import static com.lulobank.credits.starter.v3.util.TimeConverter.toUTC;
import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class PaymentHandlerTest {
    @Mock
    private PaymentUseCase paymentUseCase;
    private PaymentHandler paymentHandler;
    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;
    private final LocalDateTime time = now();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentHandler = new PaymentHandler(paymentUseCase);
    }

    @Test
    public void paymentSuccess() {
        when(paymentUseCase.execute(paymentCaptor.capture())).thenReturn(Either.right(paymentResponse(time)));
        ResponseEntity<AdapterResponse> responseEntity = paymentHandler.makePayment(paymentRequest(), ID_CLIENT);
        assertThat(responseEntity.getStatusCode(), is(OK));
        assertThat(paymentCaptor.getValue().getAmount().doubleValue(), is(AMOUNT));
        assertThat(paymentCaptor.getValue().getClientId(), is(ID_CLIENT));
        PaymentResponse paymentResponse = (PaymentResponse) responseEntity.getBody();
        assertPaymentResponse(paymentResponse);

    }

    @Test
    public void paymentFailed() {

        when(paymentUseCase.execute(any())).thenReturn(Either.left(new UseCaseResponseError("CRE_106", "502")));
        ResponseEntity<AdapterResponse> responseEntity = paymentHandler.makePayment(paymentRequest(), ID_CLIENT);
        assertThat(responseEntity.getStatusCode(), is(NOT_ACCEPTABLE));
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_106"));
        assertThat(errorResponse.getDetail(), is("D"));
    }

    private void assertPaymentResponse(PaymentResponse paymentResponse) {
        assertThat(paymentResponse.getAmountPaid(),is(BigDecimal.valueOf(3000000d)));
        assertThat(paymentResponse.getDate(),is(toUTC(time)));
        assertThat(paymentResponse.getTransactionId(),is("transaction_id"));
    }

}

package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.usecase.payment.TotalPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.command.TotalPaymentInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Constants.ID_LOAN;
import static com.lulobank.credits.starter.utils.Samples.totalMinimumRequest;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class TotalPaymentHandlerTest {


    @Mock
    private TotalPaymentUseCase totalPaymentUseCase;
    private TotalPaymentHandler totalPaymentHandler;
    @Captor
    private ArgumentCaptor<TotalPaymentInstallment> paymentInstallmentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        totalPaymentHandler = new TotalPaymentHandler(totalPaymentUseCase);
    }

    @Test
    public void makePayment() {
        when(totalPaymentUseCase.execute(paymentInstallmentCaptor.capture())).thenReturn(Either.right(true));
        ResponseEntity<AdapterResponse> responseEntity = totalPaymentHandler.makePayment(totalMinimumRequest(), ID_CLIENT,new HttpHeaders());
        assertThat(responseEntity.getStatusCode(), is(ACCEPTED));
        assertThat(paymentInstallmentCaptor.getValue().getAmount().doubleValue(), is(AMOUNT));
        assertThat(paymentInstallmentCaptor.getValue().getClientId(), is(ID_CLIENT));
        assertThat(paymentInstallmentCaptor.getValue().getCoreCbsId(), is(ID_LOAN));
    }

    @Test
    public void makePaymentFailedSince() {

        when(totalPaymentUseCase.execute(paymentInstallmentCaptor.capture())).thenReturn(Either.left(new UseCaseResponseError("CRE_106", "502")));
        ResponseEntity<AdapterResponse> responseEntity = totalPaymentHandler.makePayment(totalMinimumRequest(), ID_CLIENT,new HttpHeaders());
        assertThat(responseEntity.getStatusCode(), is(NOT_ACCEPTABLE));
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse.getCode(), is("CRE_106"));
        assertThat(errorResponse.getDetail(), is("D"));

    }
}

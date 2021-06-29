package com.lulobank.credits.starter.v3.adapters.in.payment;

import com.google.common.collect.ImmutableList;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.handler.PaymentHandler;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.paymentRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

public class PaymentInboundAdapterTest {

    @Mock
    private PaymentHandler paymentHandler;

    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    private PaymentInboundAdapter paymentInboundAdapter;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        paymentInboundAdapter = new PaymentInboundAdapter(paymentHandler);
    }

    @Test
    public void paymentSuccess() {
        when(paymentHandler.makePayment(any(),anyString())).thenReturn(new ResponseEntity<>(ACCEPTED));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.makePayment(ID_CLIENT, paymentRequest());
        assertThat(responseEntity.getStatusCode(),is(ACCEPTED));
    }

    @Test
    public void paymentFailed() {
        ErrorResponse errorResponse = getErrorResponse();
        when(paymentHandler.makePayment(any(),anyString())).thenReturn(ResponseEntity.status(BAD_GATEWAY).body(errorResponse));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.makePayment(ID_CLIENT, paymentRequest());
        assertThat(responseEntity.getStatusCode(),is(BAD_GATEWAY));
    }

    @Test
    public void paymentValidationsFailed() {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("idCredit", "idCredit is null or empty")));
        ErrorResponse response = paymentInboundAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));
        Assert.assertThat(response, notNullValue());
        Assert.assertThat(response.getFailure(), Matchers.is("400"));
        Assert.assertThat(response.getDetail(), Matchers.is("V"));
        Assert.assertThat(response.getCode(), Matchers.is("CRE_104"));
    }

    public ErrorResponse getErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("502");
        errorResponse.setDetail("Fondos insuficientes pago no recibido");
        return errorResponse;
    }
}

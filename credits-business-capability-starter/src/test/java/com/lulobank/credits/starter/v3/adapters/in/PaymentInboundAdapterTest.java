package com.lulobank.credits.starter.v3.adapters.in;

import com.google.common.collect.ImmutableList;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.CustomPaymentHandler;
import com.lulobank.credits.starter.v3.handler.MinPaymentHandler;
import com.lulobank.credits.starter.v3.handler.TotalPaymentHandler;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.paymentCustomRequest;
import static com.lulobank.credits.starter.utils.Samples.paymentMinimumRequest;
import static com.lulobank.credits.starter.utils.Samples.totalMinimumRequest;
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
    private MinPaymentHandler minPaymentHandler;
    @Mock
    private CustomPaymentHandler customPaymentHandler;
    @Mock
    private TotalPaymentHandler totalPaymentHandler;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    private PaymentInboundAdapter paymentInboundAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        paymentInboundAdapter = new PaymentInboundAdapter(minPaymentHandler,customPaymentHandler,totalPaymentHandler);
    }

    @Test
    public void MinimumPaymentTest() {
        when(minPaymentHandler.makePayment(any(),anyString())).thenReturn(new ResponseEntity<>(ACCEPTED));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.minimumPayment(new HttpHeaders(), ID_CLIENT, paymentMinimumRequest());
        assertThat(responseEntity.getStatusCode(),is(ACCEPTED));
    }

    @Test
    public void MinimumPaymentFailed() {
        ErrorResponse errorResponse = getErrorResponse();
        when(minPaymentHandler.makePayment(any(),anyString())).thenReturn(ResponseEntity.status(BAD_GATEWAY).body(errorResponse));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.minimumPayment(new HttpHeaders(), ID_CLIENT, paymentMinimumRequest());
        assertThat(responseEntity.getStatusCode(),is(BAD_GATEWAY));
    }

    @Test
    public void CustomPaymentTest() {
        when(customPaymentHandler.makePayment(any(),anyString())).thenReturn(new ResponseEntity<>(ACCEPTED));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.customPayment(new HttpHeaders(), ID_CLIENT, paymentCustomRequest());
        assertThat(responseEntity.getStatusCode(),is(ACCEPTED));
    }

    @Test
    public void CustomPaymentFailed() {
        ErrorResponse errorResponse = getErrorResponse();
        when(customPaymentHandler.makePayment(any(),anyString())).thenReturn(ResponseEntity.status(BAD_GATEWAY).body(errorResponse));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.customPayment(new HttpHeaders(), ID_CLIENT, paymentCustomRequest());
        assertThat(responseEntity.getStatusCode(),is(BAD_GATEWAY));
    }

    @Test
    public void TotalPaymentTest() {
        when(totalPaymentHandler.makePayment(any(),anyString(),any())).thenReturn(new ResponseEntity<>(ACCEPTED));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.totalPayment(new HttpHeaders(), ID_CLIENT, totalMinimumRequest());
        assertThat(responseEntity.getStatusCode(),is(ACCEPTED));
    }

    @Test
    public void TotalPaymentFailed() {
        ErrorResponse errorResponse = getErrorResponse();
        when(totalPaymentHandler.makePayment(any(),anyString(),any())).thenReturn(ResponseEntity.status(BAD_GATEWAY).body(errorResponse));
        ResponseEntity<AdapterResponse> responseEntity = paymentInboundAdapter.totalPayment(new HttpHeaders(), ID_CLIENT, totalMinimumRequest());
        assertThat(responseEntity.getStatusCode(),is(BAD_GATEWAY));
    }


    @Test
    public void shouldReturnErrorWhenBindingResultNotEmpty() {
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
        errorResponse.setCode("CRE_001");
        errorResponse.setDetail("P_CB");
        return errorResponse;
    }
}

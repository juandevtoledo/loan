package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.LoanPaymentPlanHandler;
import com.lulobank.credits.starter.v3.handler.PaymentPlanHandler;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.io.IOException;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.utils.Samples.paymentPlanRequest;
import static com.lulobank.credits.starter.utils.Samples.paymentPlanResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentPlanAdapterTest {

    @Mock
    private PaymentPlanHandler paymentPlanHandler;
    @Mock
    private LoanPaymentPlanHandler loanPaymentPlanHandler;
    @Mock
    private BindingResult bindingResult;
    private PaymentPlanAdapter paymentPlanAdapter;

    @Before
    public void setUp()  {
        MockitoAnnotations.openMocks(this);
        paymentPlanAdapter = new PaymentPlanAdapter(paymentPlanHandler, loanPaymentPlanHandler);
    }

    @Test
    public void responseEntity_WhenHandlerResponseSuccess() throws IOException {
        when(paymentPlanHandler.getPaymentPlan(any(), anyString(),any())).thenReturn(AdapterResponseUtil.ok(paymentPlanResponse()));
        ResponseEntity<AdapterResponse> responseResponseEntity = paymentPlanAdapter.getPaymentPlan(ID_CLIENT, paymentPlanRequest(), bindingResult);
        assertThat(responseResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void getPaymentPlanActiveCredit_WhenHandlerResponseSuccess() throws IOException {
        when(loanPaymentPlanHandler.getPaymentPlan(any())).thenReturn(AdapterResponseUtil.ok(paymentPlanResponse()));
        ResponseEntity<AdapterResponse> responseResponseEntity = paymentPlanAdapter.getPaymentPlanActiveLoan(ID_CLIENT);
        assertThat(responseResponseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void getPaymentPlanActiveCredit_WhenHandlerResponseError() {
        when(loanPaymentPlanHandler.getPaymentPlan(any())).thenReturn(ResponseEntity.notFound().build());
        ResponseEntity<AdapterResponse> responseEntity = paymentPlanAdapter.getPaymentPlanActiveLoan(ID_CLIENT);
        assertThat(responseEntity.getStatusCode(), is(NOT_FOUND));
    }
}

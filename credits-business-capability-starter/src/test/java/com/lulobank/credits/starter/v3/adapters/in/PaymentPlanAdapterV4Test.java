package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.sdk.dto.errorv3.GenericResponse;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV4;
import com.lulobank.credits.starter.utils.Constants;
import com.lulobank.credits.starter.utils.Samples;
import com.lulobank.credits.v3.dto.ErrorUseCaseV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.usecase.PaymentPlantV3UseCase;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT_SIMULATE;
import static com.lulobank.credits.starter.utils.Constants.INTEREST_RATE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Deprecated
public class PaymentPlanAdapterV4Test {

    @Mock
    private PaymentPlantV3UseCase paymentPlantV3UseCase;
    @Mock
    private BindingResult bindingResult;
    private PaymentPlanRequestV3 request;
    private HttpHeaders headers;
    private PaymentPlanAdapterV3 testedClass;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        request = Samples.paymentPlanRequestV3Builder();
        headers = new HttpHeaders();
        testedClass = new PaymentPlanAdapterV3(paymentPlantV3UseCase);
    }

    @Test
    public void should_return_payment_plan() {
        PaymentV3 paymentV3 = Samples.paymentV3Builder();
        when(paymentPlantV3UseCase.execute(any())).thenReturn(Either.right(paymentPlanV3Builder(Arrays.asList(paymentV3))));
        ResponseEntity<PaymentPlanResponseV4> responseEntity = testedClass.getPaymentPlanV4(headers, Constants.ID_CLIENT, request, bindingResult);
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertFalse("PaymentPlan List is not empty", responseEntity.getBody().getPaymentPlan().isEmpty());
        assertThat("Http Status is right", responseEntity.getBody().getPrincipalDebit(), is(AMOUNT_SIMULATE));
        assertThat("Http Status is right", responseEntity.getBody().getMonthlyNominalRate(), is(INTEREST_RATE));
        assertThat("Http Status is right", responseEntity.getBody().getAnnualNominalRate(), is(INTEREST_RATE));
        assertThat("Http Status is right", responseEntity.getBody().getStartDate(), is(LocalDate.now()));
        assertThat("Http Status is right", responseEntity.getBody().getEndDate(), is(LocalDate.now()));
        responseEntity.getBody().getPaymentPlan().forEach(paymentPlanV3Response -> {
            assertThat("Installment right", paymentPlanV3Response.getInstallment(), is(paymentV3.getInstallment()));
            assertThat("TotalDue is right", paymentPlanV3Response.getTotalDue(), is(paymentV3.getTotalDue()));
            assertThat("DueDate is right", paymentPlanV3Response.getDueDate(), is(paymentV3.getDueDate()));
            assertThat("FeesDue is right", paymentPlanV3Response.getFeesDue(), is(paymentV3.getFeesDue()));
            assertThat("PrincipalDue is right", paymentPlanV3Response.getPrincipalDue(), is(paymentV3.getPrincipalDue()));
            assertThat("InterestDue is right", paymentPlanV3Response.getInterestDue(), is(paymentV3.getInterestDue()));
        });
    }

    @Test
    public void should_return_business_error() {
        PaymentV3 paymentV3 = Samples.paymentV3Builder();
        when(paymentPlantV3UseCase.execute(any())).thenReturn(Either.right(paymentPlanV3Builder(Arrays.asList(paymentV3))));
        when(paymentPlantV3UseCase.execute(any())).thenReturn(Either.left(new ErrorUseCaseV3("Error in coreBancking", 502, "406")));
        ResponseEntity<GenericResponse> responseEntity = testedClass.getPaymentPlanV4(headers, Constants.ID_CLIENT, request, bindingResult);
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
        assertThat("Code is ok", responseEntity.getBody().getError().getCode(), is("406"));
    }

    @Test
    public void should_return_bad_request_error() {
        PaymentV3 paymentV3 = Samples.paymentV3Builder();
        when(paymentPlantV3UseCase.execute(any())).thenReturn(Either.right(paymentPlanV3Builder(Arrays.asList(paymentV3))));
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("IdCredit", "Empty", "")));
        ResponseEntity<GenericResponse> responseEntity = testedClass.getPaymentPlanV4(headers, Constants.ID_CLIENT, request, bindingResult);
        assertThat("Http Status is right", responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat("Code is ok", responseEntity.getBody().getError().getFailure(), is("INVALID_REQUEST"));
    }

    private static PaymentPlanUseCaseResponseV3 paymentPlanV3Builder(List<PaymentV3> paymentV3s) {
        PaymentPlanUseCaseResponseV3 paymentPlanUseCaseResponse = new PaymentPlanUseCaseResponseV3();
        paymentPlanUseCaseResponse.setPrincipalDebit(AMOUNT_SIMULATE);
        paymentPlanUseCaseResponse.setMonthlyNominalRate(INTEREST_RATE);
        paymentPlanUseCaseResponse.setAnnualNominalRate(INTEREST_RATE);
        paymentPlanUseCaseResponse.setEndDate(LocalDate.now());
        paymentPlanUseCaseResponse.setStartDate(LocalDate.now());
        paymentPlanUseCaseResponse.setPaymentPlan(paymentV3s);
        return paymentPlanUseCaseResponse;
    }
}

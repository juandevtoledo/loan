package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanResponse;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.usecase.paymentplan.PaymentPlantUseCase;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static com.lulobank.credits.starter.utils.Samples.paymentPlanRequest;
import static com.lulobank.credits.starter.v3.util.EntitiesFactory.PaymentPlanFactory.paymentPlanUseCaseResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class PaymentPlanHandlerTest {

    @Mock
    private PaymentPlantUseCase paymentPlantUseCase;
    @Mock
    private BindingResult bindingResult;
    private PaymentPlanHandler paymentPlanHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        paymentPlanHandler = new PaymentPlanHandler(paymentPlantUseCase);
    }

    @Test
    public void handlerResponse_WhenUseCaseResponseSuccess() throws IOException {
        when(paymentPlantUseCase.execute(any())).thenReturn(Either.right(paymentPlanUseCaseResponse()));
        ResponseEntity<AdapterResponse> response = paymentPlanHandler.getPaymentPlan( paymentPlanRequest(),UUID.randomUUID().toString(),bindingResult);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        assertPaymentPlanResponseIsValid(response);
    }



    @Test
    public void HandlerResponse_WhenUseCaseResponseCoreBankingError() throws IOException {
        when(paymentPlantUseCase.execute(any())).thenReturn(Either.left(CoreBankingError.simulateLoanError("186")));
        ResponseEntity<AdapterResponse> response = paymentPlanHandler.getPaymentPlan( paymentPlanRequest(),UUID.randomUUID().toString(),bindingResult);
        assertThat(response.getStatusCode(),is(HttpStatus.BAD_GATEWAY));
        assertErrorResponse(response,"CRE_110","P_CB","186");

    }

    @Test
    public void HandlerResponse_WhenBingindErrorResultHasError() throws IOException {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("field_1","error","")).toJavaList());
        when(paymentPlantUseCase.execute(any())).thenReturn(Either.left(CoreBankingError.simulateLoanError("186")));
        ResponseEntity<AdapterResponse> response = paymentPlanHandler.getPaymentPlan( paymentPlanRequest(),UUID.randomUUID().toString(),bindingResult);
        assertThat(response.getStatusCode(),is(HttpStatus.BAD_REQUEST));
        assertErrorResponse(response,"CRE_104","V","400");

    }

    private void assertErrorResponse(ResponseEntity<AdapterResponse> response,String code, String detail, String failure) {
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertThat(errorResponse.getCode(),is(code));
        assertThat(errorResponse.getDetail(),is(detail));
        assertThat(errorResponse.getFailure(),is(failure));
    }


    private void assertPaymentPlanResponseIsValid(ResponseEntity<AdapterResponse> response) {
        PaymentPlanResponse paymentPlanResponse = (PaymentPlanResponse) response.getBody();
        assertThat(paymentPlanResponse.getPaymentPlan().size(),is(4));
        assertThat(paymentPlanResponse.getMonthlyNominalRate(),is(1.16F));
        assertThat(paymentPlanResponse.getInterestRate(),is(16.5F));
        assertThat(paymentPlanResponse.getStartDate(),is(LocalDate.parse("2021-03-15")));
        assertThat(paymentPlanResponse.getEndDate(),is(LocalDate.parse("2025-02-17")));
    }
}

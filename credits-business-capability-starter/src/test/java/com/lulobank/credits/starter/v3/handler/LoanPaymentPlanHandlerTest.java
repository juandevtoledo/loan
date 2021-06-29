package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanResponse;
import com.lulobank.credits.v3.usecase.paymentplan.LoanPaymentPlantUseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static com.lulobank.credits.starter.utils.Constants.ID_CLIENT;
import static com.lulobank.credits.starter.v3.util.EntitiesFactory.PaymentPlanFactory.paymentPlanUseCaseResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;


public class LoanPaymentPlanHandlerTest {

    @Mock
    private LoanPaymentPlantUseCase loanPaymentPlantUseCase;

    private LoanPaymentPlanHandler loanPaymentPlanHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanPaymentPlanHandler = new LoanPaymentPlanHandler(loanPaymentPlantUseCase);
    }

    @Test
    public void getPaymentPlan_WhenUseCaseResponseSuccess() {
        when(loanPaymentPlantUseCase.execute(any())).thenReturn(Either.right(paymentPlanUseCaseResponse()));
        ResponseEntity<AdapterResponse> responseEntity = loanPaymentPlanHandler.getPaymentPlan(ID_CLIENT);

        assertThat(responseEntity.getStatusCode(), is(OK));
        assertPaymentPlanValidResponse(responseEntity);
    }

    @Test
    public void getPaymentPlan_WhenUseCaseResponseError() {
        when(loanPaymentPlantUseCase.execute(any())).thenReturn(Either.left(CreditsError.databaseError()));
        ResponseEntity<AdapterResponse> responseEntity = loanPaymentPlanHandler.getPaymentPlan(ID_CLIENT);

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertNotNull(errorResponse);
        assertThat(errorResponse.getCode(), is("CRE_101"));
        assertThat(errorResponse.getDetail(), is("D"));
        assertThat(errorResponse.getFailure(), is("404"));
    }

    private void assertPaymentPlanValidResponse(ResponseEntity<AdapterResponse> response) {
        PaymentPlanResponse paymentPlanResponse = (PaymentPlanResponse) response.getBody();
        assertNotNull(paymentPlanResponse);
        assertThat(paymentPlanResponse.getPaymentPlan().size(),is(4));
        assertThat(paymentPlanResponse.getMonthlyNominalRate(),is(1.16F));
        assertThat(paymentPlanResponse.getInterestRate(),is(16.5F));
        assertThat(paymentPlanResponse.getStartDate(),is(LocalDate.parse("2021-03-15")));
        assertThat(paymentPlanResponse.getEndDate(),is(LocalDate.parse("2025-02-17")));
    }
}

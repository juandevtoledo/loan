package com.lulobank.credits.v3.usecase.paymentplan;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static com.lulobank.credits.services.Constant.ID_CLIENT;
import static com.lulobank.credits.v3.util.CoreBankingFactory.LoanFactory.loanForPaymentPlan;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanPaymentPlantUseCaseTest {

    private LoanPaymentPlantUseCase loanPaymentPlantUseCase;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        loanPaymentPlantUseCase = new LoanPaymentPlantUseCase(creditsV3Repository, coreBankingService);
    }

    @Test
    public void getPaymentPlanSuccess() {
        when(creditsV3Repository.findLoanActiveByIdClient(any())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(any(), any())).thenReturn(Either.right(loanForPaymentPlan()));
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = loanPaymentPlantUseCase.execute(ID_CLIENT);

        assertTrue(response.isRight());
        assertPaymentPlan(response.get());
        verify(creditsV3Repository, times(1)).findLoanActiveByIdClient(any());
        verify(coreBankingService, times(1)).getLoanInformation(any(), any());
    }

    @Test
    public void getPaymentPlan_WhenCreditNotFound() {
        when(creditsV3Repository.findLoanActiveByIdClient(any())).thenReturn(Option.none());
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = loanPaymentPlantUseCase.execute(ID_CLIENT);

        assertTrue(response.isLeft());
        assertErrorResponse(response.getLeft(), "404", "CRE_101", "D");
        verify(coreBankingService, never()).getLoanInformation(any(), any());
    }

    @Test
    public void getPaymentPlan_WhenCreditNotFoundCoreBanking() {
        when(creditsV3Repository.findLoanActiveByIdClient(any())).thenReturn(Option.of(creditsEntityWithAcceptOffer()));
        when(coreBankingService.getLoanInformation(any(), any())).thenReturn(Either.left(
                CoreBankingError.buildGettingDataLoanError("502")));
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = loanPaymentPlantUseCase.execute(ID_CLIENT);

        assertTrue(response.isLeft());
        assertErrorResponse(response.getLeft(), "502", "CRE_103", "P_CB");
        verify(creditsV3Repository, times(1)).findLoanActiveByIdClient(any());
    }

    private void assertErrorResponse(UseCaseResponseError error, String code, String failure, String detail) {
        assertThat(error.getProviderCode(), is(code));
        assertThat(error.getBusinessCode(), is(failure));
        assertThat(error.getDetail(), is(detail));
    }

    private void assertPaymentPlan(PaymentPlanUseCaseResponse response) {
        assertThat(response.getPrincipalDebit(), is(BigDecimal.valueOf(1000000.00d)));
        assertThat(response.getTotalBalance(), is(BigDecimal.valueOf(734182.12d)));
        assertThat(response.getTotalBalanceExpected(), is(BigDecimal.valueOf(791100.63d)));
        assertThat(response.getPaymentPlan().size(), is(4));
    }
}

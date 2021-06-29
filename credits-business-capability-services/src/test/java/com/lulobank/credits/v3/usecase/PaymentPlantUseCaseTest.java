package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.usecase.paymentplan.PaymentPlantUseCase;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.lulobank.credits.Samples.getPaymentPlanBuilder;
import static com.lulobank.credits.Samples.offerEntityV3;
import static com.lulobank.credits.v3.util.EntitiesFactory.SimulatePaymentFactory.simulatePayments;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PaymentPlantUseCaseTest {

    private PaymentPlantUseCase paymentPlantUseCase;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;
    @Captor
    private ArgumentCaptor<SimulatePaymentRequest> simulatePaymentRequestCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        paymentPlantUseCase = new PaymentPlantUseCase(creditsV3Repository, coreBankingService);
    }

    @Test
    public void paymentPlanUseCaseResponse_WhenPaymentPlantSimulate() throws IOException {
        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.of(offerEntityV3("FLEXIBLE_LOAN")));
        when(coreBankingService.simulateLoan(simulatePaymentRequestCaptor.capture())).thenReturn(Either.right(simulatePayments()));
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = paymentPlantUseCase.execute(getPaymentPlanBuilder());
        assertContentIsValid(offerEntityV3("FLEXIBLE_LOAN"), response);
        PaymentV3 paymentPlanFirst = response.get().getPaymentPlan().stream().findFirst().get();
        assertFirstPaymentPlan(paymentPlanFirst);
        assertSimulateRequestValid(simulatePaymentRequestCaptor.getValue());
    }

    @Test
    public void paymentPlanUseCaseResponse_WhenOfferNotFound() {
        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.none());
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = paymentPlantUseCase.execute(getPaymentPlanBuilder());
        assertTrue("Exists error", response.isLeft());
        assertThat(response.getLeft().getProviderCode(), is("404"));
    }

    @Test
    public void paymentPlanUseCaseResponse_WhenCoreBankingError() throws IOException {
        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.of(offerEntityV3("FLEXIBLE_LOAN")));
        when(coreBankingService.simulateLoan(any())).thenReturn(Either.left(CoreBankingError.simulateLoanError("502")));
        Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response = paymentPlantUseCase.execute(getPaymentPlanBuilder());
        assertTrue("Exists error", response.isLeft());
        assertThat(response.getLeft().getProviderCode(), is("502"));
    }

    private void assertFirstPaymentPlan(PaymentV3 paymentPlanFirst) {
        assertThat(paymentPlanFirst.getInstallment(), is(1));
        assertThat(paymentPlanFirst.getDueDate(), is(LocalDate.parse("2021-03-15")));
        assertThat(paymentPlanFirst.getFeesDue(), is(BigDecimal.valueOf(6500)));
        assertThat(paymentPlanFirst.getTotalDue(), is(BigDecimal.valueOf(84561.13)));
        assertThat(paymentPlanFirst.getInterestDue(), is(BigDecimal.valueOf(55906.85)));
        assertThat(paymentPlanFirst.getPercentPrincipalDue(), is(26.2F));
        assertThat(paymentPlanFirst.getPercentFeesDue(), is(7.69F));
        assertThat(paymentPlanFirst.getPercentInterestDue(), is(66.12F));
        assertThat(paymentPlanFirst.getPendingBalance().doubleValue(), is(2977845.72));
    }

    private void assertContentIsValid(OfferEntityV3 offerEntityV3, Either<UseCaseResponseError, PaymentPlanUseCaseResponse> response) {
        assertThat("Don't exists error", response.isLeft(), is(false));
        assertThat("PaymentPlan is not empty", response.get().getPaymentPlan().isEmpty(), is(false));
        assertThat("PrincipalDebit is right", response.get().getPrincipalDebit().doubleValue(), is(offerEntityV3.getAmount()));
        assertThat("MonthlyNominalRate is right", response.get().getMonthlyNominalRate(), is(BigDecimal.valueOf(23.69)));
        assertThat("InterestRate is right", response.get().getInterestRate(), is(BigDecimal.valueOf(23.69)));
        assertThat("MonthlyNominalRate is right", response.get().getMonthlyNominalRate(), is(BigDecimal.valueOf(23.69)));
        assertThat("StartDate is right", response.get().getStartDate(), is(LocalDate.now()));
        assertThat("EndDate is right", response.get().getEndDate(), is(LocalDate.parse("2021-09-15")));
    }

    private void assertSimulateRequestValid(SimulatePaymentRequest simulatePaymentRequest) {
        assertThat(simulatePaymentRequest.getInterestRate(), is(BigDecimal.valueOf(23.69)));
        assertThat(simulatePaymentRequest.getAmount(), is(BigDecimal.valueOf(3000000.0)));
        assertThat(simulatePaymentRequest.getInstallment(), is(48));
        assertThat(simulatePaymentRequest.getDayOfPay(), is(15));
    }

}

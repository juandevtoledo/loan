package com.lulobank.credits.v3.usecase;

import com.lulobank.credits.v3.dto.ErrorUseCaseV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.credits.v3.port.in.loan.dto.LoanV3Error;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lulobank.credits.Constants.DAY_OF_PAY;
import static com.lulobank.credits.Constants.FESS_DUE;
import static com.lulobank.credits.Constants.INTEREST_DUE;
import static com.lulobank.credits.Constants.PERCENT_FEE_DUE;
import static com.lulobank.credits.Constants.PERCENT_INTEREST_DUE;
import static com.lulobank.credits.Constants.PERCENT_PRINCIPAL_DUE;
import static com.lulobank.credits.Constants.PRINCIPAL_DUE;
import static com.lulobank.credits.Constants.TOTAL_DUE;
import static com.lulobank.credits.Samples.getPaymentPlanBuilder;
import static com.lulobank.credits.Samples.offerEntityV3;
import static com.lulobank.credits.Samples.simulatePaymentsBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Deprecated
public class PaymentPlantV3UseCaseTest {

    private PaymentPlantV3UseCase testClass;
    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private LoanV3Service loanV3Service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testClass = new PaymentPlantV3UseCase(creditsV3Repository, loanV3Service);
    }

    @Test
    public void should_return_payment_plan_list() throws IOException {
        OfferEntityV3 offerEntityV3 = offerEntityV3("COMFORTABLE_LOAN");
        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.of(offerEntityV3));
        List<SimulatePayment> simulatePayments = simulatePaymentsBuilder();
        when(loanV3Service.simulateLoan(any())).thenReturn(Either.right(simulatePayments));
        Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3> response = testClass.execute(getPaymentPlanBuilder());
        assertFalse("Don't exists error", response.isLeft());
        assertFalse("PaymentPlan is not empty", response.get().getPaymentPlan().isEmpty());
        assertThat("PrincipalDebit is right", response.get().getPrincipalDebit().doubleValue(), is(offerEntityV3.getAmount()));
        assertThat("MonthlyNominalRate is right", response.get().getMonthlyNominalRate(), is(offerEntityV3.getMonthlyNominalRate()));
        assertThat("AnnualNominalRate is right", response.get().getAnnualNominalRate(), is(offerEntityV3.getInterestRate().floatValue()));
        assertThat("StartDate is right", response.get().getStartDate(), is(LocalDate.now()));
        assertThat("EndDate is right", response.get().getEndDate(), is(LocalDate.now()));
        response.get().getPaymentPlan().stream().findFirst().ifPresent(paymentPlanV3 -> {
            assertThat(paymentPlanV3.getInstallment(), is(1));
            assertThat(paymentPlanV3.getDueDate(), is(LocalDate.now()));
            assertThat(paymentPlanV3.getFeesDue(), is(FESS_DUE));
            assertThat(paymentPlanV3.getTotalDue(), is(TOTAL_DUE));
            assertThat(paymentPlanV3.getInterestDue(), is(INTEREST_DUE));
            assertThat(paymentPlanV3.getPercentPrincipalDue(), is(PERCENT_PRINCIPAL_DUE));
            assertThat(paymentPlanV3.getPercentFeesDue(), is(PERCENT_FEE_DUE));
            assertThat(paymentPlanV3.getPercentInterestDue(), is(PERCENT_INTEREST_DUE));
            assertThat(paymentPlanV3.getPendingBalance().doubleValue(), is(0d));
        });
    }

    @Test
    public void should_return_error_since_offer_not_found() {
        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.none());
        Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3> response = testClass.execute(getPaymentPlanBuilder());
        assertTrue("Exists error", response.isLeft());
        assertThat(response.getLeft().getCode(), is(404));
    }

    @Test
    public void should_return_error_since_error_in_corebanking() throws IOException {

        when(creditsV3Repository.findOfferEntityV3ByIdClient(any(), any(UUID.class), any())).thenReturn(Option.of(offerEntityV3("COMFORTABLE_LOAN")));
        when(loanV3Service.simulateLoan(any())).thenReturn(Either.left(new LoanV3Error("502", "error in core bancking")));
        Either<ErrorUseCaseV3, PaymentPlanUseCaseResponseV3> response = testClass.execute(getPaymentPlanBuilder());
        assertTrue("Exists error", response.isLeft());
        assertThat(response.getLeft().getCode(), is(502));
    }

    public static List<SimulatePayment> simulatePaymentsBuilder() {
        List<SimulatePayment> simulatePayments = new ArrayList<>();
        simulatePayments.add(simulatePaymentBuilder());
        simulatePayments.add(simulatePaymentBuilder());
        return simulatePayments;
    }

    public static SimulatePayment simulatePaymentBuilder() {
        SimulatePayment simulatePayment = new SimulatePayment();
        simulatePayment.setDueDate(LocalDateTime.now());
        simulatePayment.setFeesDue(FESS_DUE);
        simulatePayment.setTotalDue(TOTAL_DUE);
        simulatePayment.setInterestDue(INTEREST_DUE);
        simulatePayment.setPrincipalDue(PRINCIPAL_DUE);
        return simulatePayment;
    }

}

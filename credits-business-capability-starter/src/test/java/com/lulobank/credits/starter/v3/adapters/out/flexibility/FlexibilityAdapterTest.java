package com.lulobank.credits.starter.v3.adapters.out.flexibility;

import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.dto.*;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.connector.ProviderException;
import flexibility.client.models.request.GetLoanRequest;
import flexibility.client.models.response.ConfigValueResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Either;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static com.lulobank.credits.starter.utils.Constants.AMOUNT;
import static com.lulobank.credits.starter.utils.Samples.*;
import static com.lulobank.credits.starter.v3.util.FlexibilityFactory.AccountsInformationFactory.buildListAccountResponse;
import static com.lulobank.credits.starter.v3.util.FlexibilityFactory.AccountsInformationFactory.buildListAccountResponseError;
import static com.lulobank.credits.starter.v3.util.FlexibilityFactory.LoanInformationFactory.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FlexibilityAdapterTest {

    private final String INSURANCE_FEE_KEY = "loan.insurrance.rate";

    private static final Double INSTALMENT_TOTAL_DUE = 3000000d;
    private static final Double TOTAL_BALANCE = 50000.0;
    private static final String ID_CLIENT_MAMBU = "1999982388";
    private static final String ID_ACCOUNT_MAMBU = "1999982388";

    @Captor
    private ArgumentCaptor<GetLoanRequest> loanRequestCaptor;

    @Mock
    private FlexibilitySdk flexibilitySdk;
    @Mock
    private FunctionBrave functionBrave;
    @Mock
    CreditsConditionV3 creditsConditionV3;

    CircuitBreaker circuitBreaker;

    private FlexibilityAdapter flexibilityAdapter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        CreditsConditionDomain creditsConditionDomain = new CreditsConditionDomain();
        creditsConditionDomain.setFeeAmountInstallement(1d);

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slowCallDurationThreshold(Duration.ofSeconds(1))
                .slowCallRateThreshold(50)
                .slidingWindowSize(2)
                .minimumNumberOfCalls(1)
                .ignoreExceptions(ProviderException.class)
                .build();

        circuitBreaker = CircuitBreaker.of("flexibility", circuitBreakerConfig);
        flexibilityAdapter = new FlexibilityAdapter(flexibilitySdk, functionBrave, creditsConditionV3, circuitBreaker);
    }

    @Test
    public void shouldReturnInsuranceFeeOk() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> of(buildConfig("0.0026")));

        Either<CoreBankingError, Double> insuranceFee = flexibilityAdapter.getInsuranceFee();

        assertThat(insuranceFee.isRight(), is(true));
        assertThat(insuranceFee.get(), is(0.0026d));
    }

    @Test
    public void shouldNotReturnInsuranceFee_WhenCircuitBreakerOpen() {

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
        });

        Either<CoreBankingError, Double> response = flexibilityAdapter.getInsuranceFee();

        assertThat(response.isLeft(),is(true));
        assertThatGettingInfoError(response.getLeft());
    }

    @Test
    public void shouldNotReturnInsuranceFeeWhenConfigListEmpty() throws ProviderException {
        when(flexibilitySdk.getConfig(INSURANCE_FEE_KEY)).thenReturn(emptyList());

        Either<CoreBankingError, Double> insuranceFee = flexibilityAdapter.getInsuranceFee();

        assertThat(insuranceFee.isLeft(), is(true));

        CoreBankingError error = insuranceFee.getLeft();
        assertThat(error, notNullValue());
        assertThatGettingInfoError(error);
    }

    @Test
    public void shouldNotReturnInsuranceFeeWhenConfigIsNotANumber() throws ProviderException {
        when(flexibilitySdk.getConfig(INSURANCE_FEE_KEY)).thenReturn(of(buildConfig("ERROR_VALUE")));

        Either<CoreBankingError, Double> insuranceFee = flexibilityAdapter.getInsuranceFee();

        assertThat(insuranceFee.isLeft(), is(true));

        CoreBankingError error = insuranceFee.getLeft();
        assertThat(error, notNullValue());
        assertThatGettingInfoError(error);
    }

    @Test
    public void shouldNotReturnInsuranceFeeWhenProviderException() throws ProviderException {
        when(flexibilitySdk.getConfig(INSURANCE_FEE_KEY)).thenThrow(new ProviderException("Error core banking", ""));

        Either<CoreBankingError, Double> insuranceFee = flexibilityAdapter.getInsuranceFee();

        assertThat(insuranceFee.isLeft(), is(true));

        CoreBankingError error = insuranceFee.getLeft();
        assertThat(error, notNullValue());
        assertThatGettingInfoError(error);
    }

    @Test
    public void paymentSuccess() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> getPaymentResponse());
        Either<CoreBankingError, PaymentApplied> response = flexibilityAdapter.payment(createPayment());
        assertThat(response.isRight(),is(true));
        assertPaymentApplied(response.get());

    }


    @Test
    public void paymentFailedSinceProviderError() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw  new ProviderException("Error", "502");
        });
        Either<CoreBankingError, PaymentApplied> response = flexibilityAdapter.payment(createPayment());
        assertThat(response.isLeft(),is(true));
        assertThatPaymentError(response.getLeft());
    }

    @Test
    public void payment_WhenCircuitBreakerOpen() {

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
        });

        Either<CoreBankingError, PaymentApplied> response = flexibilityAdapter.payment(createPayment());

        assertThat(response.isLeft(), is(true));
        assertThatProviderError(response.getLeft());
    }

    @Test
    public void getLoanInformationFail() throws ProviderException {
        when(flexibilitySdk.getLoanByLoanAccountId(loanRequestCaptor.capture())).thenThrow(new ProviderException("error","500"));
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw  new ProviderException("Error", "502");
        });
        Either<CoreBankingError, LoanInformation> response = flexibilityAdapter.getLoanInformation(ID_ACCOUNT_MAMBU,
                ID_CLIENT_MAMBU);

        assertThat(response.isLeft(), is(true));
        assertThatGettingInfoError(response.getLeft());
    }

    @Test
    public void getLoanInformationSuccess() throws ProviderException {
        when(flexibilitySdk.getLoanByLoanAccountId(loanRequestCaptor.capture())).thenReturn(buildGetLoanResponse());
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e->buildGetLoanResponse());
        Either<CoreBankingError, LoanInformation> response = flexibilityAdapter.getLoanInformation(ID_ACCOUNT_MAMBU,
                ID_CLIENT_MAMBU);

        assertThat(response.isRight(), is(true));
        assertThat(response.isLeft(), is(false));
        assertThat(response.get().getInstallmentExpectedDue().getValue(), is(BigDecimal.valueOf(INSTALMENT_TOTAL_DUE)));
        assertThat(response.get().getTotalBalance().getValue(), is(BigDecimal.valueOf(TOTAL_BALANCE)));
        assertThat(response.get().getInstallmentExpected().getValue(), is(BigDecimal.valueOf(INSTALMENT_TOTAL_DUE)));
        assertThat(response.get().getInstallmentAccrued().getValue(), is(BigDecimal.valueOf(INSTALMENT_TOTAL_DUE)));
        assertThat(response.get().getPaymentPlanList().isEmpty(), is(false));
        assertThat(response.get().getPaymentPlanList().size(), is(4));
    }
    
    @Test
    public void getLoanStatementSuccess() throws ProviderException {
    	circuitBreaker.reset();
    	
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e->buildGetLoanStatementResponse());
        Either<CoreBankingError, LoanStatement> response = flexibilityAdapter.getLoanStatement(ID_ACCOUNT_MAMBU,
                ID_CLIENT_MAMBU, "2020/04");
        assertThat(response.isRight(), is(true));
    }

    @Test
    public void getLoanInformation_WhenCircuitBreakerOpen() {

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
        });

        Either<CoreBankingError, LoanInformation> response = flexibilityAdapter.getLoanInformation(ID_ACCOUNT_MAMBU,
                ID_CLIENT_MAMBU);

        assertThat(response.isLeft(),is(true));
        assertThatProviderError(response.getLeft());
    }

    @Test
    public void getLoanMovementsFail() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildLoanMovementsResponseError());
        Either<CoreBankingError, List<Movement>> response = flexibilityAdapter.getLoanMovements(
                buildGetMovementsRequest());

        assertTrue(response.isLeft());
        assertThatGettingInfoError(response.getLeft());
    }

    @Test
    public void getLoanMovementsSuccess() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildLoanMovementsResponse());

        Either<CoreBankingError, List<Movement>> response = flexibilityAdapter.getLoanMovements(
                buildGetMovementsRequest());

        assertThatLoanMovementsResp(response);
    }

    @Test
    public void getLoanMovements_WhenCircuitBreakerOpen() {
        circuitBreaker.reset();

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildLoanMovementsResponse(),
                e -> buildLoanMovementsResponseDelayed(), e -> buildLoanMovementsResponseDelayed());

        Either<CoreBankingError, List<Movement>> res1 = flexibilityAdapter.getLoanMovements(
                buildGetMovementsRequest());
        Either<CoreBankingError, List<Movement>> res2 = flexibilityAdapter.getLoanMovements(
                buildGetMovementsRequest());
        Either<CoreBankingError, List<Movement>> res3 = flexibilityAdapter.getLoanMovements(
                buildGetMovementsRequest());

        assertThatLoanMovementsResp(res1);
        assertThatLoanMovementsResp(res2);
        assertTrue(res3.isLeft());
        assertThatProviderError(res3.getLeft());
        assertThatCircuitBreakerMetrics();
    }

    @Test
    public void getAccountsByClientFail() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildListAccountResponseError());
        Either<CoreBankingError, List<ClientAccount>> response = flexibilityAdapter.getAccountsByClient(
                ID_CLIENT_MAMBU);

        assertTrue(response.isLeft());
        assertThat(response.getLeft().getBusinessCode(), is("CRE_107"));
        assertThat(response.getLeft().getProviderCode(), is("502"));
    }

    @Test
    public void getAccountsByClientSuccess() {
        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> buildListAccountResponse());

        Either<CoreBankingError, List<ClientAccount>> response = flexibilityAdapter.getAccountsByClient(
                ID_CLIENT_MAMBU);

        assertThatListAccountResp(response);
    }

    @Test
    public void getAccountsByClient_WhenCircuitBreakerOpen() {

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
        });

        Either<CoreBankingError, List<ClientAccount>> response = flexibilityAdapter.getAccountsByClient(
                ID_CLIENT_MAMBU);

        assertThat(response.isLeft(),is(true));
        assertThatProviderError(response.getLeft());
    }

    @Test
    public void simulateLoan_WhenCircuitBreakerOpen() {

        when(functionBrave.decorateChecked(any(), any())).thenReturn(e -> {
            throw CallNotPermittedException.createCallNotPermittedException(circuitBreaker);
        });

        Either<CoreBankingError, List<SimulatePayment>> response = flexibilityAdapter.simulateLoan(
                buildSimulatePaymentRequest());

        assertThat(response.isLeft(),is(true));
        assertThatProviderError(response.getLeft());
    }

    private void assertThatLoanMovementsResp(Either<CoreBankingError, List<Movement>> res) {
        assertTrue(res.isRight());
        assertThat(res.get().size(), Is.is(5));
    }

    private void assertThatProviderError(CoreBankingError error) {
        assertThat(error.getBusinessCode(), is("CRE_108"));
        assertThat(error.getDetail(), is("P_CB"));
        assertThat(error.getProviderCode(), is("502"));
    }

    private void assertThatGettingInfoError(CoreBankingError error) {
        assertThat(error.getBusinessCode(), is("CRE_103"));
        assertThat(error.getDetail(), is("P_CB"));
        assertThat(error.getProviderCode(), is("502"));
    }

    private void assertThatPaymentError(CoreBankingError error) {
        assertThat(error.getBusinessCode(), is("CRE_106"));
        assertThat(error.getDetail(), is("P_CB"));
        assertThat(error.getProviderCode(), is("502"));
    }

    private void assertThatCircuitBreakerMetrics() {
        assertThat(circuitBreaker.getMetrics().getNumberOfSlowCalls(), is(1));
        assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls(), is(2));
        assertThat(circuitBreaker.getMetrics().getNumberOfNotPermittedCalls(), is(1L));
    }

    private ConfigValueResponse buildConfig(String value) {
        ConfigValueResponse configValueResponse = new ConfigValueResponse();
        configValueResponse.setName(INSURANCE_FEE_KEY);
        configValueResponse.setValue(value);
        return configValueResponse;
    }

    private void assertThatListAccountResp(Either<CoreBankingError, List<ClientAccount>> res) {
        assertTrue(res.isRight());
        assertThat(res.get().size(), Is.is(1));
    }

    private void assertPaymentApplied(PaymentApplied response) {
        assertThat(response.getAmount().doubleValue(),is(AMOUNT));
        assertThat(response.getEntryDate().toLocalDate(),is(LocalDate.now()));
        assertThat(response.getTransactionId(),is("transaction-id"));
    }
}

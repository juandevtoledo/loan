package com.lulobank.credits.v3.usecase.preappoveloanoffers;

import com.lulobank.credits.v3.dto.RiskResult;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.service.CalculateFlexibleInstallmentService;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.SimulateByRiskResponse;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.command.GetOffersByIdClient;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.Offer;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntitWithRiskResponsev2;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

public class PreapprovedLoanOffersUseCaseTest {

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(500000.0);
    private static final BigDecimal FIRST_INSTALLMENT_VALUE = BigDecimal.valueOf(46525.95);

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;
    private PreapprovedLoanOffersUseCase preapprovedLoanOffersUseCase;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CalculateFlexibleInstallmentService calculateFlexibleInstallmentService = new CalculateFlexibleInstallmentService();
        preapprovedLoanOffersUseCase = new PreapprovedLoanOffersUseCase(creditsV3Repository, coreBankingService, new SimulateByRiskResponse(calculateFlexibleInstallmentService));
    }

    @Test
    public void offers_WhenIsValidCredit() {
        CreditsV3Entity creditsV3Entity = creditsEntitWithRiskResponsev2();
        when(creditsV3Repository.findByIdClient(any())).thenReturn(List.of(creditsV3Entity));
        when(creditsV3Repository.save(any())).thenReturn(Try.of(() -> creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026));
        Either<UseCaseResponseError, OfferedResponse> response = preapprovedLoanOffersUseCase.execute(getRequest());
        assertTrue(response.isRight());
        OfferedResponse content = response.get();
        assertThatContentIsValid(creditsV3Entity, content);
        Offer offer = content.getOffer();
        assertThatFirstOfferIsValid(creditsV3Entity, offer);
    }

    @Test
    public void offers_WhenCreditNotFound() {
        when(creditsV3Repository.findByIdClient(any())).thenReturn(List.empty());
        Either<UseCaseResponseError, OfferedResponse> response = preapprovedLoanOffersUseCase.execute(getRequest());
        assertError(response, "CRE_101", "404");
    }


    @Test
    public void offers_WhenPersistError() {
        when(creditsV3Repository.findByIdClient(any())).thenReturn(List.of(creditsEntitWithRiskResponsev2()));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026));
        when(creditsV3Repository.save(any())).thenReturn(Try.failure(new RuntimeException()));
        Either<UseCaseResponseError, OfferedResponse> response = preapprovedLoanOffersUseCase.execute(getRequest());
        assertError(response, "CRE_110", "502");
    }

    @Test
    public void offers_WhenInitialsOffersEmpty() {
        when(creditsV3Repository.findByIdClient(any())).thenReturn(List.of(creditsWithInitialsOfferEmpty()));
        Either<UseCaseResponseError, OfferedResponse> response = preapprovedLoanOffersUseCase.execute(getRequest());
        assertError(response, "CRE_101", "404");
    }

    private RiskResult getRiskResult(CreditsV3Entity creditsV3Entity) {
        return creditsV3Entity.getInitialOffer().getResults().stream().findFirst().get();
    }

    private GetOffersByIdClient getRequest() {
        return new GetOffersByIdClient(UUID.randomUUID().toString(), AMOUNT);
    }

    private CreditsV3Entity creditsWithInitialsOfferEmpty() {
        CreditsV3Entity creditsV3Entity = creditsEntitWithRiskResponsev2();
        creditsV3Entity.setInitialOffer(null);
        return creditsV3Entity;
    }

    private void assertThatFirstOfferIsValid(CreditsV3Entity creditsV3Entity, Offer offer) {
        assertThat(offer.getAmount(), is(AMOUNT));
        assertThat(offer.getIdOffer(), is(creditsV3Entity.getInitialOffer().getOfferEntities().stream().findFirst().get().getIdOffer()));
        assertThat(offer.getSimulateInstallment().size(), is(37));
        assertThat(offer.getType(),is(OffersTypeV3.FLEXIBLE_LOAN.name()));
        assertThat(offer.getSimulateInstallment().stream().findFirst().get().getAmount(), is(FIRST_INSTALLMENT_VALUE));
    }

    private void assertThatContentIsValid(CreditsV3Entity creditsV3Entity, OfferedResponse content) {
        assertThat(content.getIdCredit(), is(creditsV3Entity.getIdCredit().toString()));
        assertThat(content.getAmount(), is(AMOUNT));
        assertThat(content.getMaxAmountInstallment().doubleValue(), is(getRiskResult(creditsV3Entity).getMaxAmountInstallment().doubleValue()));
        assertThat(content.getMaxTotalAmount().doubleValue(), is(getRiskResult(creditsV3Entity).getMaxTotalAmount().doubleValue()));
    }

    private void assertError(Either<UseCaseResponseError, OfferedResponse> response, String businessCode, String providerCode) {
        assertTrue(response.isLeft());
        assertThat(response.getLeft().getBusinessCode(), is(businessCode));
        assertThat(response.getLeft().getProviderCode(), is(providerCode));
        assertThat(response.getLeft().getDetail(), is("D"));
    }
}

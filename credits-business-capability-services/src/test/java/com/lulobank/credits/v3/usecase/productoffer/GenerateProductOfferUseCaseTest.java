package com.lulobank.credits.v3.usecase.productoffer;

import com.lulobank.credits.v3.port.in.productoffer.dto.Offer;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.port.in.productoffer.dto.SimulatedInstallment;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.service.CalculateFlexibleInstallmentService;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.service.SimulateByFormulaService;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static com.lulobank.credits.Samples.generateOfferRequest;
import static com.lulobank.credits.services.Constant.ID_PRODUCT_OFFER;
import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GenerateProductOfferUseCaseTest {

    @Mock
    private CreditsV3Repository creditsV3Repository;
    @Mock
    private CoreBankingService coreBankingService;
    @Mock
    private SimulateByFormulaService simulateByFormulaServiceMock;
    private GenerateProductOfferUseCase generateProductOfferUseCase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SimulateByFormulaService simulateByFormulaService = new SimulateByFormulaService(new CalculateFlexibleInstallmentService());
        generateProductOfferUseCase = new GenerateProductOfferUseCase(creditsV3Repository, simulateByFormulaService,
                coreBankingService);
    }

    @Test
    public void shouldReturnOfferGeneratedOk() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();

        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026d));
        when(creditsV3Repository.save(creditsV3Entity)).thenReturn(Try.success(creditsV3Entity));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isRight(), is(true));

        ProductOffer response = productOffer.get();
        assertThat(response, notNullValue());
        assertThat(response.getAmount(), is(11000000d));
        assertThat(response.getCurrentDate(), notNullValue());
        assertThat(response.getIdCredit(), is("ebdab0bf-699b-48e8-ab0f-f339d8a61090"));
        assertThat(response.getOffers(), hasSize(1));
        assertOffer(response.getOffers().get(0), 37);
        assertEntity(creditsV3Entity);

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verify(coreBankingService).getInsuranceFee();
        verify(creditsV3Repository).save(creditsV3Entity);
    }

    @Test
    public void shouldReturnOfferGeneratedOkWithFilteredInstallments() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();
        creditsV3Entity.getInitialOffer().getRiskEngineAnalysis().setMaxAmountInstallment(600000d);

        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026d));
        when(creditsV3Repository.save(creditsV3Entity)).thenReturn(Try.success(creditsV3Entity));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isRight(), is(true));

        ProductOffer response = productOffer.get();
        assertThat(response, notNullValue());
        assertThat(response.getAmount(), is(11000000d));
        assertThat(response.getCurrentDate(), notNullValue());
        assertThat(response.getIdCredit(), is("ebdab0bf-699b-48e8-ab0f-f339d8a61090"));
        assertThat(response.getOffers(), hasSize(1));
        assertOffer(response.getOffers().get(0), 26);
        assertEntity(creditsV3Entity);

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verify(coreBankingService).getInsuranceFee();
        verify(creditsV3Repository).save(creditsV3Entity);
    }

    @Test
    public void shouldNotReturnOfferGeneratedWhenCreditDoesNotExists() {
        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.none());

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("404"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verifyNoMoreInteractions(coreBankingService);
        verify(creditsV3Repository, times(0)).save(any(CreditsV3Entity.class));
    }

    @Test
    public void shouldNotReturnOfferGeneratedWhenCreditTypeIsNotPreApproved() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();
        creditsV3Entity.setCreditType(null);
        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("404"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verifyNoMoreInteractions(coreBankingService);
        verify(creditsV3Repository, times(0)).save(any(CreditsV3Entity.class));
    }

    @Test
    public void shouldNotReturnOfferGeneratedWhenLoanRequestedAmountGreaterThanRiskAnalysisAmount() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();
        creditsV3Entity.getInitialOffer().getRiskEngineAnalysis().setAmount(5000000d);
        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("404"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verifyNoMoreInteractions(coreBankingService);
        verify(creditsV3Repository, times(0)).save(any(CreditsV3Entity.class));
    }

    @Test
    public void shouldNotReturnOfferGeneratedWhenCoreBankingError() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();

        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.left(CoreBankingError.getParametersError()));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_103"));
        assertThat(error.getDetail(), is("P_CB"));
        assertThat(error.getProviderCode(), is("502"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verify(coreBankingService).getInsuranceFee();
        verify(creditsV3Repository, times(0)).save(any(CreditsV3Entity.class));
    }

    @Test
    public void shouldNotReturnOfferGeneratedWhenSimulateByFormulaError() {
        generateProductOfferUseCase = new GenerateProductOfferUseCase(creditsV3Repository, simulateByFormulaServiceMock,
                coreBankingService);
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();
        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026d));
        doReturn(Option.none()).when(simulateByFormulaServiceMock).build(any(OffersTypeV3.class), any(OfferInformationRequest.class));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_102"));
        assertThat(error.getDetail(), is("U"));
        assertThat(error.getProviderCode(), is("406"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verify(coreBankingService).getInsuranceFee();
        verify(creditsV3Repository, times(0)).save(any(CreditsV3Entity.class));
    }


    @Test
    public void shouldNotReturnOfferGeneratedWhenSaveEntityError() {
        CreditsV3Entity creditsV3Entity = CreditsEntityFactory.creditsEntityOfferPreApproved();

        when(creditsV3Repository.findByIdProductOffer(ID_PRODUCT_OFFER)).thenReturn(Option.of(creditsV3Entity));
        when(coreBankingService.getInsuranceFee()).thenReturn(Either.right(0.0026d));
        when(creditsV3Repository.save(creditsV3Entity)).thenReturn(Try.failure(new UnsupportedOperationException("operation not supported")));

        Either<UseCaseResponseError, ProductOffer> productOffer = generateProductOfferUseCase.execute(generateOfferRequest());

        assertThat(productOffer.isLeft(), is(true));

        UseCaseResponseError error = productOffer.getLeft();
        assertThat(error, notNullValue());
        assertThat(error.getBusinessCode(), is("CRE_101"));
        assertThat(error.getDetail(), is("D"));
        assertThat(error.getProviderCode(), is("404"));

        verify(creditsV3Repository).findByIdProductOffer(ID_PRODUCT_OFFER);
        verify(coreBankingService).getInsuranceFee();
        verify(creditsV3Repository).save(any(CreditsV3Entity.class));
    }

    private void assertEntity(CreditsV3Entity creditsV3Entity) {
        assertThat(creditsV3Entity.getLoanRequested(), notNullValue());
        assertThat(creditsV3Entity.getLoanRequested().getAmount(), is(11000000d));
        assertThat(creditsV3Entity.getInitialOffer().getAmount(), is(11000000d));
        assertThat(creditsV3Entity.getInitialOffer().getGenerateDate(), notNullValue());
        assertThat(creditsV3Entity.getInitialOffer().getOfferEntities(), hasSize(1));
    }

    private void assertOffer(Offer firstOffer, int installmentSize) {
        assertThat(firstOffer.getAmount(), is(11000000d));
        assertThat(firstOffer.getIdOffer(), notNullValue());
        assertThat(firstOffer.getInsuranceCost(), is(0.0026d));
        assertThat(firstOffer.getInterestRate(), is(16.5f));
        assertThat(firstOffer.getMonthlyNominalRate(), is(1.28f));
            assertThat(firstOffer.getName(), is("Cr\u00e9dito personalizado"));
        assertThat(firstOffer.getType(), is("FLEXIBLE_LOAN"));
        assertThat(firstOffer.getSimulateInstallment(), hasSize(installmentSize));

        SimulatedInstallment installment = firstOffer.getSimulateInstallment().get(0);
        assertThat(installment.getAmount(), is(336805.30));
        assertThat(installment.getInstallment(), is(48));
    }
}
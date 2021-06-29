package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import com.lulobank.credits.v3.util.EntitiesFactory;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static com.lulobank.credits.v3.port.out.corebanking.CoreBankingError.simulateLoanError;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferInformationRequestFactory.OfferInformationRequestBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SimulateServiceTest {

    @Mock
    private CoreBankingService coreBankingService;
    private SimulateService simulateService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        simulateService = new SimulateService(coreBankingService);
    }

    @Test
    public void createOfferOk() {

        List<SimulatePayment> listSimulate = Collections.singletonList(EntitiesFactory.SimulatePaymentFactory.simulatePaymentResponse());
        when(coreBankingService.simulateLoan(any())).thenReturn(Either.right(listSimulate));
        Option<OfferEntityV3> offerEntityV3s = simulateService.createOffer(OffersTypeV3.FAST_LOAN, OfferInformationRequestBuilder().build());
        assertThat(offerEntityV3s.isEmpty(), is(false));
        assertThat(offerEntityV3s.get().getAmount(), is(2434568.0d));
        assertThat(offerEntityV3s.get().getAmountInstallment(), is(300000d));
    }

    @Test
    public void failedSinceRiskAmountLessThanSimulateAmount() {
        OfferInformationRequest offerInformationRequest = OfferInformationRequestBuilder().clientMonthlyAmountCapacity(100d).build();
        List<SimulatePayment> listSimulate = Collections.singletonList(EntitiesFactory.SimulatePaymentFactory.simulatePaymentResponse());
        when(coreBankingService.simulateLoan(any())).thenReturn(Either.right(listSimulate));
        Option<OfferEntityV3> offerEntityV3s = simulateService.createOffer(OffersTypeV3.FAST_LOAN, offerInformationRequest);
        assertThat(offerEntityV3s.isEmpty(), is(true));
    }

    @Test
    public void failedSinceSimulateServiceFailed() {

        InitialOfferV3 initialOfferV3 = EntitiesFactory.InitialsOfferFactory.initialsOfferInDB();
        initialOfferV3.getRiskEngineAnalysis().setMaxAmountInstallment(100d);
        when(coreBankingService.simulateLoan(any())).thenReturn(Either.left(simulateLoanError("502")));
        Option<OfferEntityV3> offerEntityV3s = simulateService.createOffer(OffersTypeV3.FAST_LOAN, OfferInformationRequestBuilder().build());
        assertThat(offerEntityV3s.isEmpty(), is(true));
    }
}

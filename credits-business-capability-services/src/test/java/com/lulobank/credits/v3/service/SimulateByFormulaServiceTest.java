package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.service.dto.OfferInformationRequest;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.lulobank.credits.v3.service.OffersTypeV3.FLEXIBLE_LOAN;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferInformationRequestFactory.OfferInformationRequestBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimulateByFormulaServiceTest {

    public static final BigDecimal AMOUNT_FIRST_INSTALLMENT = BigDecimal.valueOf(76003.96);
    private SimulateByFormulaService simulateByFormulaServiceTest;

    @Before
    public void setUp() throws Exception {
        simulateByFormulaServiceTest = new SimulateByFormulaService(new CalculateFlexibleInstallmentService());
    }

    @Test
    public void getFlexibleOffer() {
        Option<OfferEntityV3> offerEntityV3 = simulateByFormulaServiceTest.build(FLEXIBLE_LOAN, OfferInformationRequestBuilder().build());
        assertThat(offerEntityV3.get().getType(), is(FLEXIBLE_LOAN.name()));
        assertThat(offerEntityV3.get().getFlexibleLoans().isEmpty(), is(false));
        assertThat(offerEntityV3.get().getIdOffer(), notNullValue());
        assertThat(offerEntityV3.get().getFlexibleLoans().size(), is(37));
        assertThat(offerEntityV3.get().getFlexibleLoans().stream().findFirst().get().getAmount(), is(AMOUNT_FIRST_INSTALLMENT));
    }

    @Test
    public void failedSinceRequestIsNull() {
        Option<OfferEntityV3> offerEntityV3 = simulateByFormulaServiceTest.build(FLEXIBLE_LOAN, null);
        assertThat(offerEntityV3.isEmpty(), is(true));
    }


}
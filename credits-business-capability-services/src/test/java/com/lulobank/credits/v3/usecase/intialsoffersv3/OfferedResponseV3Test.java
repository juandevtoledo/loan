package com.lulobank.credits.v3.usecase.intialsoffersv3;

import com.lulobank.credits.v3.dto.InitialOfferV3;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.CO;
import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.KO;
import static com.lulobank.credits.v3.usecase.intialsoffersv3.OfferResponseV3.OK;
import static com.lulobank.credits.v3.util.EntitiesFactory.InitialsOfferFactory.initialsOfferInDB;
import static com.lulobank.credits.v3.util.EntitiesFactory.OfferFactory.createOfferEntityFlexibleV3Valid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OfferedResponseV3Test {


    @Test
    public void getOkResponseV3Test() {
        InitialOfferV3 initialOfferV3 = initialsOfferInDB();
        initialOfferV3.setAmount(2000d);
        initialOfferV3.setClientLoanRequestedAmount(BigDecimal.valueOf(1000d));
        OfferResponseV3 offerResponseV3 = OfferResponseV3.get(initialOfferV3);
        assertThat(offerResponseV3, is(OK));
    }

    @Test
    public void getCOResponseV3Test() {
        InitialOfferV3 initialOfferV3 = initialsOfferInDB();
        initialOfferV3.getRiskEngineAnalysis().setAmount(999d);
        initialOfferV3.setClientLoanRequestedAmount(BigDecimal.valueOf(1000d));
        OfferResponseV3 offerResponseV3 = OfferResponseV3.get(initialOfferV3);
        assertThat(offerResponseV3, is(CO));
    }

    @Test
    public void getkOResponseV3Test() {
        InitialOfferV3 initialOfferV3 = initialsOfferInDB();
        initialOfferV3.getOfferEntities().clear();
        OfferResponseV3 offerResponseV3 = OfferResponseV3.get(initialOfferV3);
        assertThat(offerResponseV3, is(KO));
    }

    @Test
    public void getKOSinceOnlyFlexibleOffer() {
        InitialOfferV3 initialOfferV3 = new InitialOfferV3();
        initialOfferV3.setOfferEntities(Collections.singletonList(createOfferEntityFlexibleV3Valid()));
        OfferResponseV3 offerResponseV3 = OfferResponseV3.get(initialOfferV3);
        assertThat(offerResponseV3, is(KO));
    }


}

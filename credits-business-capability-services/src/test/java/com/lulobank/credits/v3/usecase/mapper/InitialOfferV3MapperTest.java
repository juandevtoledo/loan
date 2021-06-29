package com.lulobank.credits.v3.usecase.mapper;

import com.lulobank.credits.services.Constant;
import com.lulobank.credits.v3.dto.InitialOfferV3;
import com.lulobank.credits.v3.usecase.intialsoffersv3.mapper.InitialOfferV3Mapper;
import org.junit.Test;

import java.math.BigDecimal;

import static com.lulobank.credits.v3.util.EntitiesFactory.InitialsOfferFactory.initialsOfferRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InitialOfferV3MapperTest {

    @Test
    public void InitialOfferV3MapperTest() {

        InitialOfferV3 initialOfferV3 = InitialOfferV3Mapper.INSTANCE.initialOfferV3To(initialsOfferRequest());
        assertThat(initialOfferV3.getRiskEngineAnalysis().getAmount(), is(Constant.AMOUNT_LOAN));
        assertThat(initialOfferV3.getClientLoanRequestedAmount(), is(BigDecimal.valueOf(Constant.AMOUNT_LOAN)));
    }
}

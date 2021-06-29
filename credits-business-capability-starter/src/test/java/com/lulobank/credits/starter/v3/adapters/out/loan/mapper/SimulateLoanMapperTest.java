package com.lulobank.credits.starter.v3.adapters.out.loan.mapper;

import com.lulobank.credits.starter.utils.Constants;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import flexibility.client.models.request.SimulatedLoanRequest;
import flexibility.client.models.response.SimulatedLoanResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static com.lulobank.credits.starter.utils.Constants.AMOUNT_SIMULATE;
import static com.lulobank.credits.starter.utils.Constants.DEFAULT_CURRENCY;
import static com.lulobank.credits.starter.utils.Constants.PRODUCT_ID;
import static com.lulobank.credits.starter.utils.Samples.credistConditionV3Builder;
import static com.lulobank.credits.starter.utils.Samples.repaymentBuilder;
import static com.lulobank.credits.starter.utils.Samples.simulatePaymentRequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimulateLoanMapperTest {

    private CreditsConditionV3 creditsConditionV3;

    @Before
    public void setup() {
        creditsConditionV3 = credistConditionV3Builder();
        creditsConditionV3.setLoanProductId(PRODUCT_ID);
        creditsConditionV3.setDefaultCurrency(DEFAULT_CURRENCY);
    }

    @Test
    public void simulateLoanRequestFrom() {
        SimulatePaymentRequest simulatePaymentRequest = simulatePaymentRequestBuilder();
        SimulatedLoanRequest simulatedLoanRequest = SimulateLoanMapper.INSTANCE.simulateLoanRequestFrom(simulatePaymentRequest, creditsConditionV3);
        assertThat(simulatedLoanRequest.getAmount().getAmount(), is(AMOUNT_SIMULATE.doubleValue()));
        assertThat(simulatedLoanRequest.getAmount().getCurrency(), is(DEFAULT_CURRENCY));
        assertThat(simulatedLoanRequest.getLoanProductId(), is(PRODUCT_ID));
    }


    @Test
    public void simulatePaymentsFrom() {
        SimulatedLoanResponse.Repayment repayment = repaymentBuilder();
        SimulatePayment simulatedLoanRequest = SimulateLoanMapper.INSTANCE.simulatePaymentsFrom(repayment);
        assertTrue(Objects.nonNull(simulatedLoanRequest));
        assertThat(simulatedLoanRequest.getTotalDue().doubleValue(), is(Constants.AMOUNT_INSTALLMENT));
        assertThat(simulatedLoanRequest.getInterestDue().doubleValue(), is(Constants.INTEREST));

    }

}

package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.services.Constant;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePaymentRequest;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimulatePaymentRequestMapperTest {

    private OfferEntityV3 offerEntityV3;
    private GetPaymentPlan getPaymentPlan;
    private Integer installment=6;

    @Before
    public void setup() {
        offerEntityV3=new OfferEntityV3();
        offerEntityV3.setAmount(Constant.AMOUNT_LOAN);
        getPaymentPlan=GetPaymentPlan.builder().build();
    }

    @Test
    public void offerEntityWithInstallment() {
        offerEntityV3.setInstallments(installment);
        SimulatePaymentRequest simulatePaymentRequest= SimulatePaymentRequestMapper.INSTANCE.simulatePaymentRequestFrom(offerEntityV3,getPaymentPlan);
        assertThat(simulatePaymentRequest.getInstallment(),is(installment)) ;
    }

    @Test
    public void offerEntityWithOutInstallment() {
        getPaymentPlan=GetPaymentPlan.builder().installments(installment).build();
        SimulatePaymentRequest simulatePaymentRequest= SimulatePaymentRequestMapper.INSTANCE.simulatePaymentRequestFrom(offerEntityV3,getPaymentPlan);
        assertThat(simulatePaymentRequest.getInstallment(),is(installment)) ;
    }

}

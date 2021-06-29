package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.dto.PaymentPlanUseCaseResponseV3;
import com.lulobank.credits.v3.dto.PaymentV3;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.lulobank.credits.Samples.offerEntityV3;
import static com.lulobank.credits.services.Sample.paymentV3Builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Deprecated
public class PaymentPlanUseCaseResponseV3MapperTest {

    @Test
    public void mapper() throws IOException {
        OfferEntityV3 offerEntityV3=offerEntityV3("COMFORTABLE_LOAN");
        List<PaymentV3> paymentV3s=Arrays.asList(paymentV3Builder());
        PaymentPlanUseCaseResponseV3 responseV3=PaymentPlanUseCaseResponseV3Mapper.INSTANCE.paymentPlanUseCaseResponseV3MapperFrom(offerEntityV3, paymentV3s);
        assertThat(responseV3.getPaymentPlan(),is(paymentV3s));
        assertThat(responseV3.getPrincipalDebit().doubleValue(),is(offerEntityV3.getAmount()));
        assertThat(responseV3.getAnnualNominalRate(),is(offerEntityV3.getInterestRate().floatValue()));
        assertThat(responseV3.getMonthlyNominalRate(),is(offerEntityV3.getMonthlyNominalRate()));
    }
}

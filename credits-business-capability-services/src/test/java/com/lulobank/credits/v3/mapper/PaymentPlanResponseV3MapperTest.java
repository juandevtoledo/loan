package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.PaymentV3;
import com.lulobank.credits.v3.port.in.loan.dto.SimulatePayment;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.lulobank.credits.Constants.FESS_DUE;
import static com.lulobank.credits.Constants.INTEREST_DUE;
import static com.lulobank.credits.Constants.PERCENT_FEE_DUE;
import static com.lulobank.credits.Constants.PERCENT_INTEREST_DUE;
import static com.lulobank.credits.Constants.PERCENT_PRINCIPAL_DUE;
import static com.lulobank.credits.Constants.PRINCIPAL_DUE;
import static com.lulobank.credits.Constants.TOTAL_DUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Deprecated
public class PaymentPlanResponseV3MapperTest {

    @Test
    public void PaymentPlanV3Mapper_whenRequestOk() {
        PaymentV3 paymentV3 = PaymentPlanV3Mapper.INSTANCE.paymentPlanV3From(simulatePaymentBuilder(),  10);
        assertThat(paymentV3.getPercentFeesDue(), is(PERCENT_FEE_DUE));
        assertThat(paymentV3.getPercentInterestDue(), is(PERCENT_INTEREST_DUE));
        assertThat(paymentV3.getPercentPrincipalDue(), is(PERCENT_PRINCIPAL_DUE));
        assertThat(paymentV3.getDueDate(), is(LocalDate.now()));
        assertThat(paymentV3.getInstallment(), is(10));
    }

    public static SimulatePayment simulatePaymentBuilder() {
        SimulatePayment simulatePayment = new SimulatePayment();
        simulatePayment.setDueDate(LocalDateTime.now());
        simulatePayment.setFeesDue(FESS_DUE);
        simulatePayment.setTotalDue(TOTAL_DUE);
        simulatePayment.setInterestDue(INTEREST_DUE);
        simulatePayment.setPrincipalDue(PRINCIPAL_DUE);
        return simulatePayment;
    }

}

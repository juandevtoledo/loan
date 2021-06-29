package com.lulobank.credits.services;

import com.lulobank.credits.services.utils.InterestUtil;
import org.junit.Test;

import java.math.BigDecimal;

import static com.lulobank.credits.services.utils.InterestUtil.getAnnualEffectiveRateFromAnnualNominalRate;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterestUtilTest {

    @Test
    public void getMonthlyNominalRate() {
        BigDecimal interestRate = BigDecimal.valueOf(14.82f);
        BigDecimal monthlyNominalRateExpect = BigDecimal.valueOf(1.15833333);
        BigDecimal monthlyNominalResult = InterestUtil.getMonthlyNominalRate(interestRate);
        assertThat("Monthly nominal result is ok", monthlyNominalResult, is(monthlyNominalRateExpect));
    }

    @Test
    public void getMonthlyNominalRateZero() {
        BigDecimal monthlyNominalRateExpect = new BigDecimal(0).setScale(2, ROUND_HALF_UP);
        BigDecimal monthlyNominalResult = InterestUtil.getMonthlyNominalRate(ZERO);
        assertThat("Monthly nominal result is ok", monthlyNominalResult.doubleValue(), is(monthlyNominalRateExpect.doubleValue()));
    }

    @Test
    public void getMonthlyNominalRateNull() {
        BigDecimal monthlyNominalResult = InterestUtil.getMonthlyNominalRate(null);
        assertThat("Monthly nominal result is ok", monthlyNominalResult.doubleValue(), is(ZERO.doubleValue()));
    }

    @Test
    public void getAnnuallyNominalRate() {
        BigDecimal interestRate = BigDecimal.valueOf(16.5f);
        BigDecimal annuallyNominalRateExpect = BigDecimal.valueOf(15.37);
        BigDecimal annuallyNominalResult = InterestUtil.getAnnualNominalRate(interestRate);
        assertThat("Annually nominal result is ok", annuallyNominalResult, is(annuallyNominalRateExpect));
    }

    @Test
    public void getMonthlyNominalRateFromAnnualNominalRate() {
        BigDecimal annualNominalRate = BigDecimal.valueOf(25f);
        BigDecimal monthlyNominalRateExpect = BigDecimal.valueOf(2.08);
        BigDecimal monthlyNominalRateResult = InterestUtil.getMonthlyNominalRateFromAnnualNominalRate(annualNominalRate);
        assertThat("Monthly nominal rate result is ok", monthlyNominalRateResult, is(monthlyNominalRateExpect));
    }

    @Test
    public void getInterestRateFromAnnualNominalRate() {
        BigDecimal annualNominalRate = BigDecimal.valueOf(25f);
        BigDecimal interestRateExpect = BigDecimal.valueOf(28.07);
        BigDecimal interestRateResult = getAnnualEffectiveRateFromAnnualNominalRate(annualNominalRate);
        assertThat("Interest rate result is ok", interestRateResult, is(interestRateExpect));
    }
}

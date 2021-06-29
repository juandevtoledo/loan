package com.lulobank.credits.services.utils;

import com.lulobank.credits.v3.util.RoundNumber;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;


public final class InterestUtil {

    private InterestUtil() {
    }

    private static final Float MONTH_OF_YEAR = 12f;
    private static final Float DAY_OF_MONTh = 30f;
    public static final double PERCENT = 100d;

    public static BigDecimal getMonthlyNominalRate(BigDecimal interestRate) {
        return getAnnualNominalRate(interestRate)
                .divide(BigDecimal.valueOf(MONTH_OF_YEAR), 8, RoundingMode.HALF_UP);
    }

    public static BigDecimal getMonthlyNominalRateFromAnnualNominalRate(BigDecimal annualNominalRate) {
        return annualNominalRate.divide(BigDecimal.valueOf(MONTH_OF_YEAR), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getAnnualNominalRate(BigDecimal interestRate) {
        return Option.of(interestRate)
                .map(interest -> interest.divide(BigDecimal.valueOf(PERCENT), 8, RoundingMode.HALF_UP))
                .map(InterestUtil::getAnnualNominalRateFromAnnualEffectiveRate)
                .map(RoundNumber::defaultScale)
                .getOrElse(ZERO);

    }

    public static BigDecimal dailyPeriodicRateToAnnual(BigDecimal dailyPeriodicRate) {
        return dailyPeriodicRate.multiply(BigDecimal.valueOf(DAY_OF_MONTh * MONTH_OF_YEAR));
    }

    /**
     * Annual effective rate % = (((1+ Annual nominal rate/m)^m) -1) * 100
     *  where Annual nominal rate = (Annual nominal rate %) / 100
     *        m = 12
     */
    public static BigDecimal getAnnualEffectiveRateFromAnnualNominalRate(BigDecimal annualNominalRate) {
        double annualEffectiveRate = (Math.pow(1 + annualNominalRate.divide(BigDecimal.valueOf(PERCENT), 8, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(MONTH_OF_YEAR), 8, RoundingMode.HALF_UP).doubleValue(), MONTH_OF_YEAR) - 1) * PERCENT;
        return BigDecimal.valueOf(annualEffectiveRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Annual nominal rate % = ((((1 + Annual effective rate)^(1/m))-1)*m) * 100
     *   where Annual effective rate = (Annual effective rate %) / 100
     *         m = 12
     */
    private static BigDecimal getAnnualNominalRateFromAnnualEffectiveRate(BigDecimal annualEffectiveRate) {
        double annualNominalRate = (Math.pow(1 + annualEffectiveRate.doubleValue(), (1 / MONTH_OF_YEAR)) - 1) * 12 * PERCENT;
        return BigDecimal.valueOf(annualNominalRate).setScale(2, RoundingMode.HALF_UP);
    }
}

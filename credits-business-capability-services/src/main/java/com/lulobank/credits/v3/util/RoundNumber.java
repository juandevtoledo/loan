package com.lulobank.credits.v3.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoundNumber {

    private RoundNumber() {
    }

    public static BigDecimal defaultScale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}

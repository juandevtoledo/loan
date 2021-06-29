package com.lulobank.credits.v3.vo.loan;

import com.lulobank.credits.v3.util.RoundNumber;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AmountVO {

    private final BigDecimal value;

    public BigDecimal getRoundValue() {
        return RoundNumber.defaultScale(value);
    }
}

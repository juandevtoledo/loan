package com.lulobank.credits.starter.v3.adapters.in.dto.loan;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Money {
    private final BigDecimal value;
    private final String currency;
}

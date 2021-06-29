package com.lulobank.credits.starter.v3.adapters.in.dto.loan;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class Rates {
    private final BigDecimal monthlyNominal;
    private final BigDecimal annualEffective;
}
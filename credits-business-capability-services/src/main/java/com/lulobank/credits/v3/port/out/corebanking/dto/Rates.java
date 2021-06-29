package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Rates {

    private final BigDecimal monthlyNominal;
    private final BigDecimal annualEffective;
}

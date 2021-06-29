package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MovementDetail {

    private final BigDecimal insuranceAmount;
    private final BigDecimal capitalAmount;
    private final BigDecimal interestAmount;
    private final BigDecimal penaltyAmount;
}

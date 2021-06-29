package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ExtraAmountResponse extends AdapterResponse {

    private final BigDecimal minimumValue;
    private final BigDecimal extraAmount;
    private final BigDecimal totalValue;
    private final String paymentType;
}

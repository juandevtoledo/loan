package com.lulobank.credits.v3.usecase.installment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ExtraAmountInstallmentResult {

    private final BigDecimal minimumValue;
    private final BigDecimal extraAmount;
    private final BigDecimal totalValue;
    private final String paymentType;
}

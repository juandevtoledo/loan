package com.lulobank.credits.v3.usecase.preappoveloanoffers.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OfferedResponse {
    private final BigDecimal amount;
    private final String idCredit;
    private final BigDecimal maxAmountInstallment;
    private final BigDecimal maxTotalAmount;
    private final Offer offer;
}

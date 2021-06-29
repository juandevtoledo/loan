package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PreapprovedLoanOffersResponse extends AdapterResponse {
    private final BigDecimal amount;
    private final String idCredit;
    private final BigDecimal maxAmountInstallment;
    private final BigDecimal maxTotalAmount;
    private final Offer offer;
}

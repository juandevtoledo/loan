package com.lulobank.credits.v3.usecase.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MakePaymentRequest {
    private final String idClient;
    private final String idCredit;
    private final String loanId;
    private final String coreBankingId;
    private final BigDecimal amount;
    private final SubPaymentType type;
}

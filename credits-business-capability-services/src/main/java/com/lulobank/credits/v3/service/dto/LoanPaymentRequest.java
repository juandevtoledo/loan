package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class LoanPaymentRequest {

    private final String idClient;
    private final String idCredit;
    private final Boolean paymentOff;
    private final BigDecimal amount;
    private final String loanId;
    private final String type;
}

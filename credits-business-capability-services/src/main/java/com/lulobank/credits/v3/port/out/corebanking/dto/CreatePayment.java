package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreatePayment {
    private final String coreBankingId;
    private final String accountId;
    private final String loanId;
    private final BigDecimal amount;
    private final Boolean payOff;
    private final TypePayment type;
}

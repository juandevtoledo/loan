package com.lulobank.credits.sdk.dto.clientloandetail;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Installment {
    private final boolean installmentPaid;
    private final boolean disableMinimumPayment;
    private final BigDecimal balance;
    private final String dueOn;
    private final String lastPaidDate;
    private final BigDecimal amount;
}

package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class LoanDetail {
    private final BigDecimal balance;
    private final BigDecimal accruedInterest;
    private final BigDecimal accruedPenalty;
    private final BigDecimal penaltyBalance;
    private final BigDecimal amountInstallment;
    private final List<InstallmentDetail> installments;
}

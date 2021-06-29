package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PaymentPlanResponse extends AdapterResponse{
    private final BigDecimal principalDebit;
    private final BigDecimal totalBalance;
    private final BigDecimal totalBalanceExpected;
    private final Float interestRate;
    private final Float monthlyNominalRate;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<Payment> paymentPlan;
}
package com.lulobank.credits.v3.usecase.paymentplan.command;

import com.lulobank.credits.v3.dto.PaymentV3;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PaymentPlanUseCaseResponse {
    private final BigDecimal principalDebit;
    private final BigDecimal totalBalance;
    private final BigDecimal totalBalanceExpected;
    private final BigDecimal monthlyNominalRate;
    private final BigDecimal interestRate;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<PaymentV3> paymentPlan;
}
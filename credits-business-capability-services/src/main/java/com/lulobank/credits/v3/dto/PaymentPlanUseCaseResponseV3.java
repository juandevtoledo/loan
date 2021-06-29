package com.lulobank.credits.v3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPlanUseCaseResponseV3 {
    private BigDecimal principalDebit;
    private Float monthlyNominalRate;
    private Float annualNominalRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PaymentV3> paymentPlan;
}

package com.lulobank.credits.sdk.dto.paymentplantv3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Deprecated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPlanResponseV4 {
    private BigDecimal principalDebit;
    private Float monthlyNominalRate;
    private Float annualNominalRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PaymentV3> paymentPlan;
}

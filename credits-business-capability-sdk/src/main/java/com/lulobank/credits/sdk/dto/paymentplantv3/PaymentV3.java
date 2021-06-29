package com.lulobank.credits.sdk.dto.paymentplantv3;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentV3 {
    private Integer installment;
    private LocalDate dueDate;
    private BigDecimal principalDue;
    private BigDecimal totalDue;
    private BigDecimal interestDue;
    private BigDecimal feesDue;
    private BigDecimal pendingBalance;
    private Float percentPrincipalDue;
    private Float percentInterestDue;
    private Float percentFeesDue;
}

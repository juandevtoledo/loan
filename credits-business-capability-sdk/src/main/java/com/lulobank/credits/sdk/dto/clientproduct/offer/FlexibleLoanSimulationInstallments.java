package com.lulobank.credits.sdk.dto.clientproduct.offer;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FlexibleLoanSimulationInstallments {

    private Integer installment;
    private BigDecimal amount;

    public FlexibleLoanSimulationInstallments() {
    }

    public FlexibleLoanSimulationInstallments(Integer installment, BigDecimal amount) {
        this.installment = installment;
        this.amount = amount;
    }
}

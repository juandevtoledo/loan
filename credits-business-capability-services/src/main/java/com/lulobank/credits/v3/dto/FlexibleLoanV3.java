package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FlexibleLoanV3 {

    private Integer installment;
    private BigDecimal amount;
    private BigDecimal monthlyNominalRate;
    private BigDecimal annualNominalRate;
    private BigDecimal interestRate;

    public static FlexibleLoanV3 empty(){
        return new FlexibleLoanV3();
    }
}

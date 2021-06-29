package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ModifiedLoan {
    private String idLoanAccountMambu;
    private BigDecimal amount;
    private LocalDateTime acceptDate;
    private BigDecimal interestRate;
    private BigDecimal monthlyNominalRate;
    private BigDecimal annualNominalRate;
    private String modificationType;
    private Integer installments;
}

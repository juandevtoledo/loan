package com.lulobank.credits.sdk.dto.loandetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDetail {
    private String idLoan;
    private String idCredit;
    private String productType;
    private Double balance;
    private String state;
    private Integer paidInstallments;
    private Integer installments;
    private NextInstallment nextInstallment;
    private Double interestRate;
    
}

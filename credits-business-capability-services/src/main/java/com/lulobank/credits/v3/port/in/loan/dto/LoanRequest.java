package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoanRequest {
    private AmountDto amount;
    private Boolean automaticDisbursement;
    private String clientId;
    private String label;
    private String productTypeKey;
    private Integer repaymentInstallments;
    private String interestRate ;
    private boolean automaticDebit;
    private Integer paymentDay ;
}

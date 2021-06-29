package com.lulobank.credits.services.inboundadapters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanAccountRequest {

    private String loanAmount;
    private Integer repaymentInstallments;
    private Integer interestSpread;
    private String expectedDisbursementDate;
}

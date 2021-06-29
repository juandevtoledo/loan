package com.lulobank.credits.services.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanAccountDomain {
    private String accountHolderKey;
    private String productTypeKey;
    private String accountHolderType;
    private Long loanAmount;
    private Integer repaymentInstallments;
    private Integer interestSpread;
    private String idLoanAccountMambu;
    private String encodedKeyLoan;
    private Integer penaltyRate;
    private String loanName;
}

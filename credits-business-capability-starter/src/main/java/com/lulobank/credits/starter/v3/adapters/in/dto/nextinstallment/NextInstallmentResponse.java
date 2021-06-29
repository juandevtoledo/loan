package com.lulobank.credits.starter.v3.adapters.in.dto.nextinstallment;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.loan.Money;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NextInstallmentResponse extends AdapterResponse {

    private final Flag flags;
    private final String idCredit;
    private final String idLoanCBS;
    private final String loanPurpose;
    private final String state;
    private final String installmentState;
    private final Money payOffAmount;
    private final String installmentDate;
    private final Money nextInstallmentAmount;
    private final Money requestedAmount;
}

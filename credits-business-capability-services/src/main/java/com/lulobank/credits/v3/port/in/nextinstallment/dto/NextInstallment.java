package com.lulobank.credits.v3.port.in.nextinstallment.dto;

import com.lulobank.credits.v3.usecase.nextinstallment.InstallmentState;
import com.lulobank.credits.v3.vo.loan.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NextInstallment {
    private final Flag flags;
    private final String idCredit;
    private final String idLoanCBS;
    private final String loanPurpose;
    private final String state;
    private final InstallmentState installmentState;
    private final Money payOffAmount;
    private final LocalDateTime installmentDate;
    private final Money nextInstallmentAmount;
    private final Money requestedAmount;
}

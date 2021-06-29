package com.lulobank.credits.starter.v3.adapters.in.dto.loan;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.usecase.loandetail.dto.PaymentPlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LoanDetailResponse extends AdapterResponse {
    private final String idCredit;
    private final String idLoanCBS;
    private final Money requestedAmount;
    private final Money payOffAmount;
    private final Money paidAmount;
    private final String state;
    private final Integer paidInstallments;
    private final Integer installments;
    private final LocalDateTime createOn;
    private final LocalDateTime closedDate;
    private final Rates rates;
    private final List<PaymentPlan> paymentPlanList;
}

package com.lulobank.credits.v3.service.mapper;

import com.lulobank.credits.v3.service.dto.InstallmentDetail;
import com.lulobank.credits.v3.service.dto.LoanDetail;
import flexibility.client.models.response.GetLoanResponse;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LoanDetailMapper {

    public static LoanDetail toLoanDetail(GetLoanResponse getLoanResponse){
        return LoanDetail.builder()
        .balance(BigDecimal.valueOf(getLoanResponse.getTotalBalance().getAmount()))
        .penaltyBalance(BigDecimal.valueOf(getLoanResponse.getPenaltyBalance()))
        .accruedInterest(BigDecimal.valueOf(getLoanResponse.getAccruedInterest()))
        .accruedPenalty(BigDecimal.valueOf(getLoanResponse.getAccruedPenalty()))
        .installments(toInstallments(getLoanResponse))
        .amountInstallment(BigDecimal.valueOf(getLoanResponse.getInstallmentTotalDue().getAmount()))
        .build();
    }


    private static List<InstallmentDetail> toInstallments(GetLoanResponse getLoanResponse) {
        return getLoanResponse.getPaymentPlanItemApiList().stream()
                .sorted(Comparator.comparing(GetLoanResponse.PaymentPlanItem::getDueDate))
                .map(paymentPlanItem ->
                        InstallmentDetail.builder()
                                .dueDate(paymentPlanItem.getDueDate())
                                .lastPaidDate(paymentPlanItem.getLastPaidDate())
                                .state(paymentPlanItem.getState())
                                .totalDue(BigDecimal.valueOf(paymentPlanItem.getTotalDue()))
                                .build()
                ).collect(Collectors.toList());
    }
}

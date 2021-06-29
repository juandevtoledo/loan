package com.lulobank.credits.v3.service.mapper;

import com.lulobank.credits.v3.service.dto.LoanPaymentRequest;
import com.lulobank.credits.v3.usecase.payment.dto.MakePaymentRequest;

import java.math.BigDecimal;

public class LoanPaymentMapper {

    public static LoanPaymentRequest loanPaymentRequestFrom(MakePaymentRequest payment) {
        return getLoanPaymentRequest(payment, payment.getAmount(), false);
    }

    public static LoanPaymentRequest loanPaymentRequestClosedFrom(MakePaymentRequest payment) {
        return getLoanPaymentRequest(payment, payment.getAmount(), true);
    }

    public static LoanPaymentRequest loanPaymentCustomAmountFrom(MakePaymentRequest payment, BigDecimal amount) {
        return getLoanPaymentRequest(payment, amount, false);
    }


    private static LoanPaymentRequest getLoanPaymentRequest(MakePaymentRequest payment, BigDecimal amount, boolean paymentOff) {
        return LoanPaymentRequest.builder()
                .loanId(payment.getLoanId())
                .idClient(payment.getIdClient())
                .idCredit(payment.getIdCredit())
                .amount(amount)
                .type(payment.getType().name())
                .paymentOff(paymentOff)
                .build();
    }
}

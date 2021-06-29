package com.lulobank.credits.starter.v3.adapters.out.flexibility.mapper;

import com.lulobank.credits.v3.port.out.corebanking.dto.CreatePayment;
import com.lulobank.credits.v3.port.out.corebanking.dto.PaymentApplied;
import flexibility.client.enums.PrepaymentRecalculationMethod;
import flexibility.client.models.request.PaymentRequest;
import flexibility.client.models.response.PaymentResponse;
import flexibility.client.util.PaymentRequestBuilder;
import io.vavr.control.Option;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentMapper {

    public static PaymentRequest paymentRequestTo(CreatePayment createPayment) {
        return
                PaymentRequestBuilder.paymentRequestBuilder()
                        .withAccountId(createPayment.getAccountId())
                        .withAmount(createPayment.getAmount().doubleValue())
                        .withCurrency("COP")
                        .withClientId(createPayment.getCoreBankingId())
                        .withPayOff(createPayment.getPayOff())
                        .withLoanAccountId(createPayment.getLoanId())
                        .withPrepaymentRecalculationMethod(getPrepaymentRecalculationMethod(createPayment.getType().getCoreBankingType()))
                        .build();
    }

    private static PrepaymentRecalculationMethod getPrepaymentRecalculationMethod(String type) {
        return Option.of(type)
                .map(PrepaymentRecalculationMethod::valueOf)
                .getOrNull();
    }

    public static PaymentApplied paymentAppliedTo(PaymentResponse paymentResponse) {
        return
                PaymentApplied.builder()
                        .amount(new BigDecimal(paymentResponse.getAmount()))
                        .status(paymentResponse.getStatus())
                        .entryDate(LocalDateTime.parse(paymentResponse.getEntryDate()))
                        .transactionId(paymentResponse.getTransactionId())
                        .build();
    }
}

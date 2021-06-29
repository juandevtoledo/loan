package com.lulobank.credits.v3.usecase.paymentplan.command;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetPaymentPlan {
    private final String idClient;
    private final UUID idCredit;
    private final String idOffer;
    private final Integer installments;
    private final Integer dayOfPay;
}

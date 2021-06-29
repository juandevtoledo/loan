package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PaymentPlanRequest {
    @NotNull(message = "IdCredit is null or empty")
    private String idCredit;
    @NotNull(message = "idOffer is null or empty")
    private String idOffer;
    @NotNull(message = "installments is null or empty")
    private Integer installments;
    @NotNull(message = "dayOfPay is null or empty")
    private Integer dayOfPay;
}

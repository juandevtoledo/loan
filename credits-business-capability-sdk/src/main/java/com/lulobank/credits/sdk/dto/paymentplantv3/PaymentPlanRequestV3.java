package com.lulobank.credits.sdk.dto.paymentplantv3;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Deprecated
@Getter
@Setter
public class PaymentPlanRequestV3 {
    private String idClient;
    @NotNull(message = "IdCredit is null or empty")
    private String idCredit;
    @NotNull(message = "idOffer is null or empty")
    private String idOffer;
    private String installments;
    @NotNull(message = "dayOfPay is null or empty")
    private Integer dayOfPay;
}

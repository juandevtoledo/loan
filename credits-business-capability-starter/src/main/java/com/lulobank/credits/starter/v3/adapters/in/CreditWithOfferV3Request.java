package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.sdk.dto.clientproduct.offer.Offer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreditWithOfferV3Request {

    @NotBlank(message = "IdCredit is null or empty")
    private String idCredit;
    private boolean automaticDebitPayments;
    @NotNull(message = "selectedCredit is null or empty")
    private Offer selectedCredit;
    @NotBlank(message = "confirmationLoanOTP is null or empty")
    private String confirmationLoanOTP;

    private Integer dayOfPay;

}

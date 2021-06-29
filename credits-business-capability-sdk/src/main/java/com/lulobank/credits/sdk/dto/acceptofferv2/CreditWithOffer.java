package com.lulobank.credits.sdk.dto.acceptofferv2;

import com.lulobank.core.Command;
import com.lulobank.credits.sdk.dto.AbstractCommandFeatures;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreditWithOffer extends AbstractCommandFeatures implements Command {
    private String idClient;
    @NotBlank(message = "IdCredit is null or empty")
    private String idCredit;
    private boolean automaticDebitPayments;
    @NotNull(message = "selectedCredit is null or empty")
    private Offer selectedCredit;
    @NotBlank(message = "confirmationLoanOTP is null or empty")
    private String confirmationLoanOTP;

    private Integer dayOfPay;
}

package com.lulobank.credits.services.inboundadapters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProductOfferResponse {

    private String idCredit;
    private String maxCreditAmount;
    private String repayment;
    private String interestRate;
}

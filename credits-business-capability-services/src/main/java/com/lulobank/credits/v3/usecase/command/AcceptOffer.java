package com.lulobank.credits.v3.usecase.command;

import com.lulobank.credits.v3.dto.OfferV3;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptOffer {

    private String idClient;
    private String idCredit;
    private boolean automaticDebitPayments;
    private OfferV3 selectedCredit;
    private String confirmationLoanOTP;
    private Integer dayOfPay;
    private AdapterCredentials credentials;
}

package com.lulobank.credits.services.utils;

import lombok.Getter;

@Getter
public enum PaymentCreditErrors {

    ERROR_PAST_OVERDRAFT("WITHDRAWAL_PAST_OVERDRAFT_CONSTRAINTS","450"),
    ERROR_BALANCE_BELOW_ZERO("BALANCE_BELOW_ZERO","401"),
    ;

    private String errorMessage;
    private String errorCode;

    PaymentCreditErrors(String errorMessage, String errorCode){
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
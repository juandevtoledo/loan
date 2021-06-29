package com.lulobank.credits.sdk.exceptions;

import lombok.Getter;

@Getter
public class InitialOffersException extends RuntimeException {
    private String message="Error try to generate Offers";
    private Integer serviceCode;
    private String serviceMessage;

    public InitialOffersException(Integer serviceCode, String serviceMessage, Throwable cause) {
        super(cause);
        this.serviceCode = serviceCode;
        this.serviceMessage = serviceMessage;
    }
    public InitialOffersException(Integer serviceCode, String serviceMessage) {
        this.serviceCode = serviceCode;
        this.serviceMessage = serviceMessage;
    }
}

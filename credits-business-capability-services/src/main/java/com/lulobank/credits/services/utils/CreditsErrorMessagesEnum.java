package com.lulobank.credits.services.utils;

public enum CreditsErrorMessagesEnum {

    BALANCE_BELOW_ZERO_401("Uy! Revisa los fondos de tu cuenta! "),
    DEFAULT_ERROR("Uy! ha ocurrido un error");


    private String message;

    CreditsErrorMessagesEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
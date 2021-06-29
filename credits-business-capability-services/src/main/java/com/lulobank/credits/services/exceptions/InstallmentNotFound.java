package com.lulobank.credits.services.exceptions;

public class InstallmentNotFound extends Exception {

    private static final String MESSAGE = "First installments is not valid to Amount simulate";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
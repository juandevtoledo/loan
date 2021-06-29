package com.lulobank.credits.services.utils;

public enum CreditsErrorResultEnum {
    CREDIT_NOT_EXIST,
    SDK_FLEXIBILITY_ERROR,
    VALIDATION_ERROR,
    INTERNAL_SERVER_ERROR,
    INTERNAL_DYNAMODB_ERROR,
    CLIENT_NOT_EXIST,
    INSTALLMENT_NOT_FOUND,
    OFFER_NOT_FOUND,
    RISK_ENGINE_ERROR,
    RISK_ENGINE_OPERATION_UNSUPPORTED,
    SAVING_ACCOUNT_SERVICE,
    SQS_ERROR,
    ACCEPT_OFFER_NOT_FOUND,
    INVALID_UUID_STRING,
    ERROR_CREATING_PROMISSORY_NOTE,
    GOOD_STANDING_ALREADY_SENT,
    INITIAL_OFFER_FOUND,
    SAVING_ACCOUNT_BLOCKED,
    FORBIDDEN_CLIENT,
    ;
}
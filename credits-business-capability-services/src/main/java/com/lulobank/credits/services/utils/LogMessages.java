package com.lulobank.credits.services.utils;

public enum LogMessages {

    ERROR_CORE_BANKING_SOLUTION("Error reported by Core Banking Middleware: {}, idClient {}"),
    INSTALLMENT_NOT_FOUND("Error trying to found installment  : {}, idClient{}"),
    ERROR_CLIENT_SERVICE("Error in client service request with Id: %s"),
    ERROR_PARSING_SQS_MESSAGE("error parsing raw sqs message"),
    ERROR_PROCESSING_SQS_MESSAGE("error processing sqs message"),
    CLIENT_EMAIL_UPDATED("Client email address of credit {} was successfully updated"),
    ERROR_CASTING_OBJECT("Error casting object : {}"),
    CLIENT_NOT_FOUND("Client not found: %s"),
    CREDIT_NOT_FOUND("Credit Not Found : {}"),
    INTERNAL_DYNAMODB_ERROR("Error in Dynamo Database : {}"),
    OFFER_NOT_FOUND("Error trying to found riskEngineOffer to credit  : {}"),
    LOAN_CREATE("Create Loan , Response : {}"),
    SERVICE_ERROR("Error found in the service : {}"),
    RISK_ENGINE_ERROR("Get Initial RiskEngineOffer from Risk Engine : {}"),
    SQS_ERROR("Error to send message to sqs : {}"),
    INTERNAL_SERVER_ERROR("General error {}"),
    SDK_FLEXIBILITY_ERROR("Flexibility error message {} - code {}"),
    SEND_GOOD_STANDING_CERTIFICATE_EVENT_INFO("Send event : {} , {}  to sqs {}"),
    OFFER_GENERATED("Offers has already generated to client  : {}"),
    ;

    private String message;

    LogMessages(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}

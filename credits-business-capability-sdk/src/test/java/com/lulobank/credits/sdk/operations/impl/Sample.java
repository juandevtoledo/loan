package com.lulobank.credits.sdk.operations.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lulobank.credits.sdk.dto.initialofferv2.ClientInformation;
import com.lulobank.credits.sdk.dto.initialofferv2.DocumentId;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.sdk.dto.initialofferv2.Phone;
import com.lulobank.credits.sdk.dto.initialofferv2.RiskEngineAnalysis;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV3;
import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanResponseV4;

import java.io.IOException;

import static org.springframework.util.ResourceUtils.getFile;

public class Sample {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Sample() {
    }

    public static GetOfferToClient getOfferToClientBuilder(Double amount, String purpose, ClientInformation clientInformation, RiskEngineAnalysis riskEngineAnalysis) {
        GetOfferToClient getOfferToClient = new GetOfferToClient();
        getOfferToClient.setClientLoanRequestedAmount(amount);
        getOfferToClient.setLoanPurpose(purpose);
        getOfferToClient.setClientInformation(clientInformation);
        getOfferToClient.setRiskEngineAnalysis(riskEngineAnalysis);
        return getOfferToClient;
    }

    public static DocumentId documentIdBuilder(String idCard, String type) {
        DocumentId documentId = new DocumentId();
        documentId.setId(idCard);
        documentId.setType(type);
        return documentId;
    }

    public static Phone phoneBuilder(String number, String prefix) {
        Phone phone = new Phone();
        phone.setNumber(number);
        phone.setPrefix(prefix);
        return phone;
    }

    public static ClientInformation clientInformationBuilder(DocumentId documentId,
                                                             Phone phone, String name, String lastName, String gender) {
        ClientInformation clientInformation = new ClientInformation();
        clientInformation.setDocumentId(documentId);
        clientInformation.setPhone(phone);
        clientInformation.setName(name);
        clientInformation.setLastName(lastName);
        clientInformation.setGender(gender);
        return clientInformation;
    }

    public static RiskEngineAnalysis riskEngineAnalysisBuilder(Double amount, Double amountInstalment, Float interestRate) {
        RiskEngineAnalysis riskEngineAnalysis = new RiskEngineAnalysis();
        riskEngineAnalysis.setAmount(amount);
        riskEngineAnalysis.setMaxAmountInstallment(amountInstalment);
        riskEngineAnalysis.setInterestRate(interestRate);
        riskEngineAnalysis.setType("dummy");
        return riskEngineAnalysis;
    }

    public static PaymentPlanResponseV3 paymentPlanBuilder() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(getFile("classpath:mocks/PaymentPlansV3.json"), PaymentPlanResponseV3.class);
    }

    public static PaymentPlanResponseV4 paymentPlanV4Builder() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(getFile("classpath:mocks/PaymentPlansV4.json"), PaymentPlanResponseV4.class);
    }
}

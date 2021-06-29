package com.lulobank.credits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.corebanking.dto.SimulatePayment;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.lulobank.credits.Constants.DAY_OF_PAY;
import static com.lulobank.credits.Constants.FESS_DUE;
import static com.lulobank.credits.Constants.INTEREST_DUE;
import static com.lulobank.credits.Constants.PRINCIPAL_DUE;
import static com.lulobank.credits.Constants.TOTAL_DUE;
import static com.lulobank.credits.services.Constant.ID_CLIENT;
import static com.lulobank.credits.services.Constant.ID_CREDIT;
import static com.lulobank.credits.services.Constant.ID_OFFER_FLEXIBLE_LOAN;
import static com.lulobank.credits.services.Constant.ID_PRODUCT_OFFER;
import static com.lulobank.credits.services.Constant.INSTALLMENT;
import static org.springframework.util.ResourceUtils.getFile;

public class Samples {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static CreditsV3Entity creditsV3EntityBuilder() throws IOException {
        return objectMapper.readValue(getFile("classpath:json/v3/creditsentity.json"), CreditsV3Entity.class);
    }

    public static OfferEntityV3 offerEntityV3(String type) throws IOException {
        switch (type) {
            case "FAST_LOAN":
                return objectMapper.readValue(getFile("classpath:json/v3/fast_offer.json"), OfferEntityV3.class);
            case "FLEXIBLE_LOAN":
                return objectMapper.readValue(getFile("classpath:json/v3/flexible_offer.json"), OfferEntityV3.class);
            default:
                return objectMapper.readValue(getFile("classpath:json/v3/comfortable_offer.json"), OfferEntityV3.class);
        }

    }

    public static SimulatePayment simulatePaymentBuilder() {
        return SimulatePayment.builder()
        .dueDate(LocalDateTime.now())
        .feesDue(FESS_DUE)
        .totalDue(TOTAL_DUE)
        .interestDue(INTEREST_DUE)
        .principalDue(PRINCIPAL_DUE)
        .build();
    }

    public static List<SimulatePayment> simulatePaymentsBuilder() {
        List<SimulatePayment> simulatePayments = new ArrayList<>();
        simulatePayments.add(simulatePaymentBuilder());
        simulatePayments.add(simulatePaymentBuilder());
        return simulatePayments;
    }

    public static GetPaymentPlan getPaymentPlanBuilder() {
        return GetPaymentPlan.builder()
                .dayOfPay(DAY_OF_PAY)
                .idClient(ID_CLIENT)
                .idCredit(UUID.fromString(ID_CREDIT))
                .idOffer(ID_OFFER_FLEXIBLE_LOAN)
                .installments(INSTALLMENT)
                .build();
    }

    public static GenerateOfferRequest generateOfferRequest() {
        GenerateOfferRequest request = new GenerateOfferRequest();
        request.setAmount(11000000d);
        request.setIdClient(ID_CLIENT);
        request.setIdProductOffer(ID_PRODUCT_OFFER);
        request.setLoanPurpose("Trip");
        return request;
    }
}

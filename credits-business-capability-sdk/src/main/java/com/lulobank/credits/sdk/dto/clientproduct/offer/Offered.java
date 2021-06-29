package com.lulobank.credits.sdk.dto.clientproduct.offer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Offered {

    private Double amount;
    private String riskModelResponse;
    private String idCredit;
    private List<Offer> offers;
    private String currentDate;

    public Offered() {
    }

    public Offered(String riskModelResponse) {
        this.riskModelResponse = riskModelResponse;
    }

    public Offered(String riskModelResponse, String idCredit, List<Offer> offers) {
        this.riskModelResponse = riskModelResponse;
        this.idCredit = idCredit;
        this.offers = offers;
    }
}
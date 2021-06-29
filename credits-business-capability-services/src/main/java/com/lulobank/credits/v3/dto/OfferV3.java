package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferV3 {

    private String idOffer;
    private Double amount;
    private Float interestRate;
    private Integer installments;
    private Double amountInstallment;
    private Double insuranceCost;
    private String type;
    private String name;
    private Float monthlyNominalRate;
}

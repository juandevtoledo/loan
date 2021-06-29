package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@DynamoDBDocument
public class OfferEntity {
    private String idOffer;
    private Double amount;
    private Integer installments;
    private Double amountInstallment;
    private String type;
    private String name;
    private List<FlexibleLoan> flexibleLoans;
    private Float interestRate;
    private Double interestAmount;
    private Double feeInsurance;
    private Double insurance;
    private BigDecimal monthlyNominalRate;
    private Float annualNominalRate;


    public OfferEntity() {

    }

    public OfferEntity(String idOffer, String type) {
        this.idOffer = idOffer;
        this.type = type;
    }

}


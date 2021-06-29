package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.lulobank.credits.services.outboundadapters.dynamoconverter.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@DynamoDBDocument
public class InitialOffer {

    private Double minAmount;
    private Double maxAmount;
    private Double maxAmountInstallment;
    private Float interestRate;
    private String typeOffer;
    private RiskEngineAnalysis riskEngineAnalysis;
    private Double amount;
    private List<OfferEntity> offerEntities;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime generateDate;
    private BigDecimal clientLoanRequestedAmount;
    private List<ResultEntity> results;

    public InitialOffer() {
        this.generateDate = LocalDateTime.now();
    }
    

}

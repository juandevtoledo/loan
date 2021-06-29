package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class RiskEngineAnalysis {
    private Double amount;
    private Float interestRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
    private String status;
}

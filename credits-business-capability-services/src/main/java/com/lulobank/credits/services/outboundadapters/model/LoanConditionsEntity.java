package com.lulobank.credits.services.outboundadapters.model;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@DynamoDBDocument
@NoArgsConstructor
public class LoanConditionsEntity {
    private Double amount;
    private Float interestRate;
    private Float defaultRate;
    private Integer installments;
    private Double maxAmountInstallment;
    private String type;
    public LoanConditionsEntity(Double amount, Float interestRate, Float defaultRate, Integer installments, Double maxAmountInstallment, String type) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.defaultRate = defaultRate;
        this.installments = installments;
        this.maxAmountInstallment = maxAmountInstallment;
        this.type = type;
    }
}
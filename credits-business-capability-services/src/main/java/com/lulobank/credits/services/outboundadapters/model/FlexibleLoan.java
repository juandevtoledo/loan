package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@DynamoDBDocument
public class FlexibleLoan {
    private Integer installment;
    private BigDecimal amount;
    private Float monthlyNominalRate;
    private Float interestRate;
    private BigDecimal annualNominalRate;

    public FlexibleLoan(){

    }

}

package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBDocument
public class Schedule {
    private Integer installment;
    private BigDecimal interestRate;
    private BigDecimal annualNominalRate;
    private BigDecimal monthlyNominalRate;
}

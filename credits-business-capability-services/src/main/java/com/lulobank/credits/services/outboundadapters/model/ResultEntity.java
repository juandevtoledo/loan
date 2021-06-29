package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBDocument
public class ResultEntity {
    private String type;
    private List<Schedule> schedule;
    private BigDecimal maxAmountInstallment;
    private BigDecimal maxTotalAmount;
    private String approved;
    private String description;
    private Double score;
    private String ruleId;
}

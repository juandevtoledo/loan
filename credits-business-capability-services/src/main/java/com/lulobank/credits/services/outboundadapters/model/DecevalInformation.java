package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class DecevalInformation {
    private Integer clientAccountId;
    private Integer promissoryNoteId;
    private String decevalCorrelationId;
    private String confirmationLoanOTP;
}

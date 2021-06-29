package com.lulobank.credits.services.outboundadapters.model;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class DocumentId {
    private String id;
    private String type;
    private String issueDate;
    private String expirationDate;
}

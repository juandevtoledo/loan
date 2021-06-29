package com.lulobank.credits.services.outboundadapters.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBDocument
public class ClientInformation {
    private DocumentId documentId;
    private String name;
    private String lastName;
    private String middleName;
    private String secondSurname;
    private String gender;
    private String email;
    private Phone phone;
}

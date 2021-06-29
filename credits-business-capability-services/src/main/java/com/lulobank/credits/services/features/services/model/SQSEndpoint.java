package com.lulobank.credits.services.features.services.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SQSEndpoint {

    private String sqsEndPointClient;
    private String sqsEndPointSavingAccount;
    private String sqsEndPointTransaction;
    private String sqsEndpointReporting;
}

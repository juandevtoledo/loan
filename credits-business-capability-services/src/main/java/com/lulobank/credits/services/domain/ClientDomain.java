package com.lulobank.credits.services.domain;


import flexibility.client.models.response.CreateClientResponse;
import flexibility.client.models.response.CreateLoanResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDomain {
    private String firstName;
    private String lastName;
    private String middleName;
    private String homePhone;
    private String emailAddress;
    private String mobilePhone1;
    private String mobilePhone2;
    private String gender;
    private String idClientMambu;
    private String accountHolderType;
    private String assignedBranchKey;
    private CreateClientResponse createClientResponse;
    private CreateLoanResponse createLoanResponse;
}

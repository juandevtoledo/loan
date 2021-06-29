package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationV3 {

    private DocumentIdV3 documentId;
    private String name;
    private String lastName;
    private String middleName;
    private String secondSurname;
    private String gender;
    private String email;
    private PhoneV3 phone;
}

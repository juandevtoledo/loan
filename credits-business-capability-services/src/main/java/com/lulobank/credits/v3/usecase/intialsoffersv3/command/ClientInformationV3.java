package com.lulobank.credits.v3.usecase.intialsoffersv3.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientInformationV3 {
    private final DocumentIdV3 documentId;
    private final String name;
    private final String lastName;
    private final String middleName;
    private final String secondSurname;
    private final String gender;
    private final String email;
    private final PhoneV3 phone;
}
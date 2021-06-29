package com.lulobank.credits.sdk.dto.initialofferv2;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ClientInformation {
    @NotNull(message = "DocumentId is null")
    private DocumentId documentId;
    @NotEmpty(message = "Name is null or empty")
    private String name;
    @NotEmpty(message = "LastName is null or empty")
    private String lastName;
    private String middleName;
    private String secondSurname;
    @NotEmpty(message = "Gender is null or empty")
    private String gender;
    @NotEmpty(message = "Email is null or empty")
    private String email;
    @NotNull(message = "Phone is null")
    private Phone phone;
}

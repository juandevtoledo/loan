package com.lulobank.credits.sdk.dto.initialofferv2;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DocumentId {
    @NotEmpty(message = "Id is null or empty")
    private String id;
    @NotEmpty(message = "Type is null or empty")
    private String type;
    private String issueDate;
    private String expirationDate;
}

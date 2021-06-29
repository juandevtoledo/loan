package com.lulobank.credits.services.inboundadapters.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoanClientResponseInvalid {

    private Integer errorCode;
    private String errorMessage;
}

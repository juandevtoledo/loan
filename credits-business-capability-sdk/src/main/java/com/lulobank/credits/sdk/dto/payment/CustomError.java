package com.lulobank.credits.sdk.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomError {

    private String failure;
    private String value;
    private String detail;

}

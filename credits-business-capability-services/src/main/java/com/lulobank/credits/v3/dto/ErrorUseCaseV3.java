package com.lulobank.credits.v3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorUseCaseV3 {
    private String message;
    private Integer code;
    private String businessCode;
    private String detail;

    public ErrorUseCaseV3(String message, Integer code, String businessCode) {
        this.message = message;
        this.code = code;
        this.businessCode = businessCode;
    }
}

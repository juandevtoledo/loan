package com.lulobank.credits.starter.v3.adapters.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends AdapterResponse {
    private String failure;
    private String code;
    private String detail;

    public ErrorResponse(String failure, String code) {
        this.failure = failure;
        this.code = code;
    }
}


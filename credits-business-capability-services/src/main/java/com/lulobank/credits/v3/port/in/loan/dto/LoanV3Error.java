package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoanV3Error {
    private String code;
    private String error;
}

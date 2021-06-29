package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanResponse {

    private String id ;
    private String accountState ;
    private String productTypeKey ;
    private String label ;
    private String settlementAccountKey ;
}

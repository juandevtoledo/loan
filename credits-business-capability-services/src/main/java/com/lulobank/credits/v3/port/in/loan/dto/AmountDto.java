package com.lulobank.credits.v3.port.in.loan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AmountDto {
    private Double amount;
    private String currency;
}

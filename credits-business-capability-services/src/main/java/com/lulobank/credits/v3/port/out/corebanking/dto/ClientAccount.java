package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ClientAccount {
    private final String number;
    private final BigDecimal balance;
    private final String status;
    private final boolean gmf;
}

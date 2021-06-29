package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMovementsRequest {
    private final String loanNumber;
    private final String clientId;
    private final Integer offset;
    private final Integer limit;
}

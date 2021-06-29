package com.lulobank.credits.v3.usecase.movement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMovements {
    private final String idClient;
    private final Integer offset;
    private final Integer limit;
}

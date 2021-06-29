package com.lulobank.credits.starter.v3.adapters.out.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteTransactionMessage {
    private final String idClient;
    private final String metadata;
}

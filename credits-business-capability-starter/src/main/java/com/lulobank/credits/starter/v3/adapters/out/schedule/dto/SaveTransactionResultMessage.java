package com.lulobank.credits.starter.v3.adapters.out.schedule.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveTransactionResultMessage {
    private final String idClient;
    private final String metadata;
    private final Result result;

    @Getter
    @Builder
    public static class Result {
        private final int day;
        private final boolean success;
    }

}

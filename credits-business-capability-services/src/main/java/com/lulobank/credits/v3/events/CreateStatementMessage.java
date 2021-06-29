package com.lulobank.credits.v3.events;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class CreateStatementMessage {
    private final String idClient;
    private final String productType;
    private final String reportType;
    private final Map<String,Object> data;
}
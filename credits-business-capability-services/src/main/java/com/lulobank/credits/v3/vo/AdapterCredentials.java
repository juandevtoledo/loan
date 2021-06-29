package com.lulobank.credits.v3.vo;

import java.util.Collections;
import java.util.Map;

public class AdapterCredentials {

    private static final String AUTHORIZATION_HEADER = "authorization";
    private final Map<String, String> headers;
    private final Map<String, Object> headersToSQS;

    public AdapterCredentials(Map<String, String> headers) {
        this.headers = getAuthorizationHeaders(headers);
        this.headersToSQS = getAuthorizationHeadersToSqs(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getHeadersToSQS() {
        return headersToSQS;
    }

    private Map<String, String> getAuthorizationHeaders(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey()))
                .findFirst()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .orElse(Collections.emptyMap());
    }

    private Map<String, Object> getAuthorizationHeadersToSqs(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(entry -> AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey()))
                .findFirst()
                .map(entry -> Collections.singletonMap(entry.getKey(), (Object)entry.getValue()))
                .orElse(Collections.emptyMap());
    }

}

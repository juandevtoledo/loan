package com.lulobank.credits.sdk.dto;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Setter
// TODO: Create this class in Core module and use it instead of this.
public abstract class AbstractCommandFeatures {
    private static final String AUTHORIZATION_HEADER_KEY = "authorization";
    protected Map<String, String> httpHeaders;

    public Map<String, String> getAuthorizationHeader() {
        return Optional.ofNullable(httpHeaders).map(header -> {
            header.keySet().removeIf(key -> !AUTHORIZATION_HEADER_KEY.equalsIgnoreCase(key));
            return header;
        }).orElse(new HashMap<>());
    }
}

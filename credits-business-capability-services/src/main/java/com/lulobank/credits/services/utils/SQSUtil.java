package com.lulobank.credits.services.utils;

import com.lulobank.credits.sdk.dto.AbstractCommandFeatures;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SQSUtil {

    private SQSUtil() {
    }

    public static <T> Map<String, Object> getMessageHeaders(T command) {
        if (command instanceof AbstractCommandFeatures) {
            return ((AbstractCommandFeatures) command).getAuthorizationHeader().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return new HashMap<>();
    }
}

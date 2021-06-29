package com.lulobank.credits.v3.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from GEN_160 onwards
 */
@Getter
@RequiredArgsConstructor
public enum GeneralErrorStatus {
    GEN_000("Unauthorized"),
    GEN_001("Invalid parameters"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "V";
}

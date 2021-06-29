package com.lulobank.credits.v3.port.out.pep;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CRE_103 to CRE_199
 */
@Getter
@RequiredArgsConstructor
public enum PepErrorStatus {
    CRE_111("Error getting data from clients"),
    CRE_113("PEP validation"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S_CL";
}

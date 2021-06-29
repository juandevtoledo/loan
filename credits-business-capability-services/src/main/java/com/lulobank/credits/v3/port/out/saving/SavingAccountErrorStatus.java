package com.lulobank.credits.v3.port.out.saving;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CRE_103 to CRE_199
 */
@Getter
@RequiredArgsConstructor
public enum SavingAccountErrorStatus {
    CRE_112("Error getting info from savings"),
    CRE_114("Account status not allowed"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S_SA";
}

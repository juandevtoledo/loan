package com.lulobank.credits.v3.port.out.corebanking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CRE_103 to CRE_199
 */
@Getter
@RequiredArgsConstructor
public enum CoreBankingErrorStatus {
    CRE_103("Error getting info from coreBanking."),
    CRE_106("Error payment in coreBanking."),
    CRE_107("Error client doesn't have account."),
    CRE_108("Error in provider Core Banking."),
    CRE_109("Error account blocked in coreBanking."),
    CRE_110("Error simulating loan in coreBanking.")
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "P_CB";
}

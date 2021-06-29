package com.lulobank.credits.v3.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CRE_180 to CRE_199
 */
@Getter
@RequiredArgsConstructor
public enum CreditsErrorStatus {
    CRE_100("Error default."),
    CRE_101("Error getting info from database."),
    CRE_102("Error generating offer."),
    CRE_103("Error getting info from coreBanking."),
    CRE_104("Validation error."),
    CRE_105("Error processing extra payment"),
    CRE_106("Error in provider, try to payment loan."),
    CRE_107("Error client doesn't have account."),
    CRE_108("Error in provider Core Banking."),
    CRE_109("Error account blocked in coreBanking."),
    CRE_110("Error in persist information in database."),
    CRE_116("Error al validar la OTP"),
	CRE_117("idCredit not found"),
	CRE_118("idOffer not found"),
	CRE_119("Error creating loan in provider"),
	CRE_120("Error getting data from client service"),
    ;

    private final String message;

    public static final String UNKNOWN_DETAIL = "U";
    public static final String DATA_DETAIL = "D";
    public static final String SERVICE_DETAIL = "S";
    public static final String PROVIDER_DETAIL = "P";
    public static final String VALIDATION_DETAIL = "V";
    public static final String OTP_VALIDATION_DETAIL = "S_OT";
    public static final String CORE_DETAIL = "S_CO";
}

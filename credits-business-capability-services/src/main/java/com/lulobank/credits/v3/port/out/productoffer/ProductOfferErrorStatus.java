package com.lulobank.credits.v3.port.out.productoffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from CRE_103 to CRE_199
 */
@Getter
@RequiredArgsConstructor
public enum ProductOfferErrorStatus {
    CRE_115("Error al modificar la info del banner"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S_CL";
}

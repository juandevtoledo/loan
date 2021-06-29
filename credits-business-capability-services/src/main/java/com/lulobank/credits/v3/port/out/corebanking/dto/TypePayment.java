package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum TypePayment {
    NONE(null),
    NUMBER_INSTALLMENTS("REDUCE_NUMBER_OF_INSTALLMENTS_NEW"),
    AMOUNT_INSTALLMENTS("REDUCE_AMOUNT_PER_INSTALLMENT"),
    ;

    private final String coreBankingType;

    TypePayment(String coreBankingType) {
        this.coreBankingType = coreBankingType;
    }

    public static TypePayment getByName(String name) {
        return Stream.of(TypePayment.values())
                .filter(typePayment -> typePayment.name().equals(name))
                .findFirst()
                .orElse(NONE);
    }

}

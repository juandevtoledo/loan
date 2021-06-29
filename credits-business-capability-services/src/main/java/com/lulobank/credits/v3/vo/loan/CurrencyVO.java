package com.lulobank.credits.v3.vo.loan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrencyVO {
    public String value;

    public static CurrencyVO defaultCurrency(){
        return CurrencyVO.builder().value("COP").build();
    }
}

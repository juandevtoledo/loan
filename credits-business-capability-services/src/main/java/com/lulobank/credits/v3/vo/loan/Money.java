package com.lulobank.credits.v3.vo.loan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Money {
    private final AmountVO amount;
    private final CurrencyVO currency;


}

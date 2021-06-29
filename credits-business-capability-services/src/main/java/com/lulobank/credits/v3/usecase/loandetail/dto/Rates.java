package com.lulobank.credits.v3.usecase.loandetail.dto;

import com.lulobank.credits.v3.vo.loan.AmountVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Rates {
    private final AmountVO monthlyNominal;
    private final AmountVO annualEffective;
}

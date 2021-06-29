package com.lulobank.credits.v3.usecase.movement.dto;

import com.lulobank.credits.v3.vo.loan.Money;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovementDetail {

    private final Money insuranceAmount;
    private final Money capitalAmount;
    private final Money interestAmount;
    private final Money penaltyAmount;
}

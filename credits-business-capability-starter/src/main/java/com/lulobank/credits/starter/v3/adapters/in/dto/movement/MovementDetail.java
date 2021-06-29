package com.lulobank.credits.starter.v3.adapters.in.dto.movement;

import com.lulobank.credits.starter.v3.adapters.in.dto.loan.Money;
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

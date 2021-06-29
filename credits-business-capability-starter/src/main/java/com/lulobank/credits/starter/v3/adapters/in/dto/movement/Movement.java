package com.lulobank.credits.starter.v3.adapters.in.dto.movement;

import com.lulobank.credits.starter.v3.adapters.in.dto.loan.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Movement {

    private final LocalDateTime due;
    private final String state;
    private final Money totalDue;
    private final MovementDetail detail;
}

package com.lulobank.credits.v3.usecase.movement.dto;

import com.lulobank.credits.v3.vo.loan.Money;
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

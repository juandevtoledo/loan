package com.lulobank.credits.v3.port.out.corebanking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Movement {

    final private LocalDateTime due;
    final private String state;
    final private BigDecimal totalDue;
    final private MovementDetail detail;
}

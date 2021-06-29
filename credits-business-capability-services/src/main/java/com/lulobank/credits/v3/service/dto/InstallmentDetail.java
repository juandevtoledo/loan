package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InstallmentDetail {
    private final String state;
    private final LocalDateTime dueDate;
    private final LocalDateTime lastPaidDate;
    private final BigDecimal totalDue;

}

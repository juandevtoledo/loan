package com.lulobank.credits.v3.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class RiskResult {
    private String type;
    private List<Schedule> schedule;
    private BigDecimal maxAmountInstallment;
    private BigDecimal maxTotalAmount;
    private String approved;
    private String description;
    private Double score;
    private String ruleId;
}

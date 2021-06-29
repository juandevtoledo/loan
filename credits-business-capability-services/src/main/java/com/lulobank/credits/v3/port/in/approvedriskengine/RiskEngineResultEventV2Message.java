package com.lulobank.credits.v3.port.in.approvedriskengine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class RiskEngineResultEventV2Message {
    private String status;
    private String idClient;
    private List<RiskResult> results;

    @Getter
    @Setter
    @ToString
    public static class RiskResult {
        private String type;
        private List<Schedule> schedule;
        private BigDecimal maxAmountInstallment;
        private BigDecimal maxTotalAmount;
        private BigDecimal loanAmount;
        private String approved;
        private String description;
        private Double score;
        private String ruleId;
    }

    @Getter
    @Setter
    @ToString
    public static class Schedule {
        private Integer installment;
        private BigDecimal interestRateEA;
        private BigDecimal interestRateNA;
        private BigDecimal interestRatePM;
    }
}
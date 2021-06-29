package com.lulobank.credits.v3.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SimulateSchedule {
    private final int intInstallment;
    private final int endInstallment;
    private final BigDecimal interestRate;
    private final BigDecimal monthlyNominalRate;
    private final BigDecimal annualNominalRate;


    public static SimulateSchedule empty(){
        return SimulateSchedule.builder().build();
    }

}

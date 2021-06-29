package com.lulobank.credits.v3.port.in.productoffer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimulatedInstallment {
    private Integer installment;
    private Double amount;
}

package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreapprovedLoanOffersRequest {

    @NotNull(message = "clientLoanRequestedAmount is null or empty")
    private BigDecimal clientLoanRequestedAmount;
}

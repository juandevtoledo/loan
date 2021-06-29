package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class PaymentRequest {
    @NotBlank(message = "idCredit is null or empty")
    private String idCredit;
    @NotBlank(message = "idCreditCBS is null or empty")
    private String idCreditCBS;
    @NotNull(message = "amount is null or empty")
    private BigDecimal amount;
}

package com.lulobank.credits.starter.v3.adapters.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPaymentRequest extends PaymentRequest {
    @NotNull(message = "type is null or empty")
    @Pattern(regexp = "AMOUNT_INSTALLMENTS|NUMBER_INSTALLMENTS")
    private String type;
}

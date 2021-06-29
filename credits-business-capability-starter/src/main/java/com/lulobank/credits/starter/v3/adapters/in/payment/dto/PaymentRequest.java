package com.lulobank.credits.starter.v3.adapters.in.payment.dto;

import com.lulobank.credits.v3.usecase.payment.dto.PaymentType;
import com.lulobank.credits.v3.usecase.payment.dto.SubPaymentType;
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
public class PaymentRequest {

    @NotBlank(message = "idCredit is null or empty")
    private String idCredit;
    @NotNull(message = "paymentType is null or empty")
    private PaymentType paymentType;
    private SubPaymentType subPaymentType;
    @NotNull(message = "amount is null or empty")
    private BigDecimal amount;
}

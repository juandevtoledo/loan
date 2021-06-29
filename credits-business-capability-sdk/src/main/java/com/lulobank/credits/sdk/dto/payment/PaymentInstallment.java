package com.lulobank.credits.sdk.dto.payment;

import com.lulobank.core.Command;
import com.lulobank.credits.sdk.dto.AbstractCommandFeatures;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PaymentInstallment extends AbstractCommandFeatures implements Command {
    private String idClient;
    @NotBlank(message = "idCredit is null or empty")
    private String idCredit;
    @NotBlank(message = "idCreditCBS is null or empty")
    private String idCreditCBS;
    @NotNull(message = "amount is null or empty")
    private Double amount;
    @NotNull(message = "paidInFull is null or empty")
    private Boolean paidInFull;
    @NotNull(message = "reduce is null or empty")
    private ReduceInstallment reduce;
}

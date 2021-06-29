package com.lulobank.credits.services.features.payment.model;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoPaymentCredit implements Command {
    private String idCredit;
    private Double amount;
}

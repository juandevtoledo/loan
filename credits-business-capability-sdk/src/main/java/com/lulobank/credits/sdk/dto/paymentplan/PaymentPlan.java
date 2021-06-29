package com.lulobank.credits.sdk.dto.paymentplan;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate due;
    private String state;
    private Double totalDue;
    private PaymentDetail detail;

    public PaymentPlan(LocalDate due, String state, Double totalDue, PaymentDetail detail) {
        this.due = due;
        this.state = state;
        this.totalDue = totalDue;
        this.detail = detail;
    }

    public PaymentPlan(){

    }
}

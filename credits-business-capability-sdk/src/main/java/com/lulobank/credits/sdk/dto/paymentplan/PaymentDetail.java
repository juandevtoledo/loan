package com.lulobank.credits.sdk.dto.paymentplan;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentDetail  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double insuranceCost;
    private Double capitalPayment;
    private Double ratePayment;
    private Double feesDue;

    public static final Double INSURANCE_COST_DEFAULT =0d;

    public PaymentDetail(Double insuranceCost, Double capitalPayment, Double ratePayment, Double feesDue) {
        this.insuranceCost = insuranceCost;
        this.capitalPayment = capitalPayment;
        this.ratePayment = ratePayment;
        this.feesDue=feesDue;
    }

    public PaymentDetail(){

    }
}

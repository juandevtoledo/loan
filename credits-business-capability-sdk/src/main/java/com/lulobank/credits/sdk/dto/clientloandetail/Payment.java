package com.lulobank.credits.sdk.dto.clientloandetail;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Payment {

    private LocalDate due;
    private String state;
    private Double totalDue;
    private PaymentDetail detail;
}


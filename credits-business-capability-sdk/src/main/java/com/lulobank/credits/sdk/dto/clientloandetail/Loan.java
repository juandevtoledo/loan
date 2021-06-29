package com.lulobank.credits.sdk.dto.clientloandetail;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan {

    private LoanDetail loanDetail;
    private Installment nextInstallment;
    private List<Payment> paymentList;
    private LocalDateTime closedDate;
}
package com.lulobank.credits.sdk.dto.clientloandetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDetail {
    private String idCredit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idCreditCBS;
    private Double amount;
    private Double balance;
    private Double paidAmount;
    private String state;
    private Integer paidInstallments;
    private Integer installments;
    private Integer dueInstallments;
    private Double monthlyInstallment;
    private Float interestRate;
    private Boolean automaticDebit;
    private String createOn;
    private BigDecimal monthlyNominalRate;
}

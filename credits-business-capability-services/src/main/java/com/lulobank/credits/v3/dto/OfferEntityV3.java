package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OfferEntityV3 {


    private String idOffer;
    private Double amount;
    private Integer installments;
    private Double amountInstallment;
    private String type;
    private String name;
    private List<FlexibleLoanV3> flexibleLoans;
    private BigDecimal interestRate;
    private Double interestAmount;
    private Double feeInsurance;
    private Double insurance;
    private BigDecimal monthlyNominalRate;
    private BigDecimal annualNominalRate;
}

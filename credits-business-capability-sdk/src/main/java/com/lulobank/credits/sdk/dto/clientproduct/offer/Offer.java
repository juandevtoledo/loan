package com.lulobank.credits.sdk.dto.clientproduct.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Offer {
    @NotBlank(message = "IdOffer is null or empty")
    private String idOffer;
    @NotNull(message = "Amount is null or empty")
    private Double amount;
    @NotNull(message = "InterestRate is null or empty")
    private Float interestRate;
    @NotNull(message = "Installments is null or empty")
    private Integer installments;
    @NotNull(message = "AmountInstallment is null or empty")
    private Double amountInstallment;
    @NotNull(message = "InsuranceCost is null or empty")
    private Double insuranceCost;
    @NotBlank(message = "Type is null or empty")
    private String type;
    @NotBlank(message = "Name is null or empty")
    private String name;
    private BigDecimal monthlyNominalRate;

}

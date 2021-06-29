package com.lulobank.credits.sdk.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InstallmentPaid implements InstallmentPaidResponse{
    private String status;
    private String idLoan;
    private String idCredit;
    private String idClient;
    private LocalDateTime acceptDate;
    private LocalDateTime closedDate;
    private BigDecimal amountOffer;
}

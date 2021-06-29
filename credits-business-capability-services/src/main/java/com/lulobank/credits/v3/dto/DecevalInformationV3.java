package com.lulobank.credits.v3.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecevalInformationV3 {
    private Integer clientAccountId;
    private Integer promissoryNoteId;
    private String decevalCorrelationId;
    private String confirmationLoanOTP;
}

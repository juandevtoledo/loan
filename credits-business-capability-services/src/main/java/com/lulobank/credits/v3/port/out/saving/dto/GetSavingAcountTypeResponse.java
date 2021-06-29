package com.lulobank.credits.v3.port.out.saving.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSavingAcountTypeResponse {
    private String idSavingAccount;
    private String state;
    private String type;
}

package com.lulobank.credits.v3.port.in.promissorynote.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromissoryNoteResponse {

    private String signPassword;
    private Integer clientAccountId;
    private Integer promissoryNoteId;
}

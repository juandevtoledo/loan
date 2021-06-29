package com.lulobank.credits.v3.usecase.automaticdebitoption.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAutomaticDebitOption {
    private final String idClient;
    private final Boolean automaticDebit;
}

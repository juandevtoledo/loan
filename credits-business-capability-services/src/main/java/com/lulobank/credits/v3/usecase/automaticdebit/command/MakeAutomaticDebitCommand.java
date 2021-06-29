package com.lulobank.credits.v3.usecase.automaticdebit.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MakeAutomaticDebitCommand {
    private final String idCredit;
    private final String metadata;
}

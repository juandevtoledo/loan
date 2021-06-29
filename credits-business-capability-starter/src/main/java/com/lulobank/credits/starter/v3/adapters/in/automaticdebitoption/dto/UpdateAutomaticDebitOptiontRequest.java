package com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAutomaticDebitOptiontRequest {
    @NotNull(message = "automaticDebit is null")
    private Boolean automaticDebit;
}

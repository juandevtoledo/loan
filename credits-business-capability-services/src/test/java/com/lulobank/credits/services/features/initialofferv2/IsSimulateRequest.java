package com.lulobank.credits.services.features.initialofferv2;

import flexibility.client.models.request.SimulatedLoanRequest;
import org.mockito.ArgumentMatcher;

import java.util.Objects;
import java.util.Optional;

public class IsSimulateRequest implements ArgumentMatcher<SimulatedLoanRequest> {

    private Integer installments;

    public IsSimulateRequest(Integer installments) {
        this.installments = installments;
    }

    @Override
    public boolean matches(SimulatedLoanRequest argument) {
        return Optional.ofNullable(argument).map(simulatedLoanRequest ->
            simulatedLoanRequest.getRepaymentInstallments().intValue() == installments
        ).orElse(false);
    }
}
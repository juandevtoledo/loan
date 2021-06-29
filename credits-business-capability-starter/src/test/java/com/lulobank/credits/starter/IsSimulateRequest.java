package com.lulobank.credits.starter;

import flexibility.client.models.request.SimulatedLoanRequest;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

public class IsSimulateRequest implements ArgumentMatcher<SimulatedLoanRequest> {

    private Integer installments;

    public IsSimulateRequest(Integer installments) {
        this.installments = installments;
    }

    @Override
    public boolean matches(SimulatedLoanRequest argument) {
        if (Objects.nonNull(argument)) {
            SimulatedLoanRequest request = argument;
            return request.getRepaymentInstallments().intValue() == installments;
        }
        return false;
    }
}

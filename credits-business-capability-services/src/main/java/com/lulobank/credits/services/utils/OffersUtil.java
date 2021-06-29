package com.lulobank.credits.services.utils;

import com.lulobank.credits.services.exceptions.InstallmentNotFound;
import flexibility.client.models.response.SimulatedLoanResponse;

public class OffersUtil {


    private OffersUtil() {
    }

    public static SimulatedLoanResponse.Repayment getFirstInstallment(SimulatedLoanResponse simulatedLoanResponse,
                                                                      Double maxAmountInstallment) throws InstallmentNotFound {

        return simulatedLoanResponse.getRepayment().stream().findFirst()
                .filter(payment -> maxAmountInstallment > payment.getTotalDue())
                .orElseThrow(InstallmentNotFound::new);
    }

}

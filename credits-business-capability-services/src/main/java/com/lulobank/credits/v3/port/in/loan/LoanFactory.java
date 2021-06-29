package com.lulobank.credits.v3.port.in.loan;

import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.dto.AmountDto;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.service.OffersTypeV3;

public class LoanFactory {

    private LoanFactory() {
    }

    public static LoanRequest createLoanRequest(LoanTransaction loanTransaction, CreditsConditionV3 creditsConditionV3) {
        AmountDto amount = AmountDto.builder()
                .amount(loanTransaction.getEntity().getAcceptOffer().getAmount())
                .currency(creditsConditionV3.getDefaultCurrency())
                .build();

        return LoanRequest.builder()
                .amount(amount)
                .automaticDebit(loanTransaction.getEntity().getAutomaticDebit())
                .automaticDisbursement(Boolean.TRUE)
                .clientId(loanTransaction.getSavingsAccountResponse().getIdCbs())
                .label(OffersTypeV3.valueOf(loanTransaction.getEntity().getAcceptOffer().getType()).getDescription())
                .productTypeKey(creditsConditionV3.getCbsProductKeyType())
                .repaymentInstallments(loanTransaction.getEntity().getAcceptOffer().getInstallments())
                .interestRate(String.valueOf(loanTransaction.getEntity().getAcceptOffer().getAnnualNominalRate()))
                .paymentDay(loanTransaction.getEntity().getDayOfPay())
                .build();

    }
}

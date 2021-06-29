package com.lulobank.credits.v3.port.savingsaccount;

import com.lulobank.credits.v3.dto.OfferEntityV3;
import com.lulobank.credits.v3.port.in.loan.LoanFactory;
import com.lulobank.credits.v3.port.in.loan.dto.LoanRequest;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.OffersTypeV3;
import com.lulobank.credits.v3.util.EntitiesFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class loanFactoryTest {

    @Test
    public void mappingLoanRequest() {
        LoanRequest loanRequest = LoanFactory.createLoanRequest(EntitiesFactory.LoanTransactionFactory.createLoanTransaction(),
                                                                EntitiesFactory.CreditsCondition.createCreditsCondition());
        CreditsV3Entity creditsV3Entity = EntitiesFactory.CreditsEntityFactory.creditsEntityWithAcceptOffer();
        OfferEntityV3 acceptOffer = creditsV3Entity.getAcceptOffer();
        assertThat(loanRequest.getAmount().getAmount(),is((acceptOffer.getAmount())));
        assertThat(loanRequest.getInterestRate(),is(String.valueOf(acceptOffer.getAnnualNominalRate())));
        assertThat(loanRequest.getLabel(),is(OffersTypeV3.valueOf(acceptOffer.getType()).getDescription()));
        assertThat(loanRequest.getRepaymentInstallments(),is(acceptOffer.getInstallments()));
        assertThat(loanRequest.getInterestRate(),is(String.valueOf(acceptOffer.getAnnualNominalRate())));

    }
}

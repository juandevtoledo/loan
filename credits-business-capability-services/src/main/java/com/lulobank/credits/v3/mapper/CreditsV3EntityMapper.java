package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.dto.LoanStatusV3;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.util.StatementsIndex;

public class CreditsV3EntityMapper {

    private CreditsV3EntityMapper() {
    }

    public static LoanTransaction mapLoanInformation(LoanTransaction loanTransaction) {
        return loanTransaction.setCreditsV3Entity(toCreditsV3Entity(loanTransaction));
    }

    private static CreditsV3Entity toCreditsV3Entity(LoanTransaction loanTransaction) {
        CreditsV3Entity creditsV3Entity = loanTransaction.getEntity();
        creditsV3Entity.setIdClientMambu(loanTransaction.getSavingsAccountResponse().getIdCbs());
        creditsV3Entity.setIdSavingAccount(loanTransaction.getSavingsAccountResponse().getAccountId());
        creditsV3Entity.setIdLoanAccountMambu(loanTransaction.getLoanResponse().getId());
        creditsV3Entity.setEncodedKeyLoanAccountMambu(loanTransaction.getLoanResponse().getProductTypeKey());
        creditsV3Entity.setLoanStatus(getLoanStatusV3(loanTransaction.getLoanResponse().getAccountState()));
        creditsV3Entity.setStatementsIndex(StatementsIndex.get(creditsV3Entity.getDayOfPay()));
        return creditsV3Entity;
    }

    private static LoanStatusV3 getLoanStatusV3(String loanState) {
        LoanStatusV3 loanStatusV3 = new LoanStatusV3();
        loanStatusV3.setStatus(loanState);
        return loanStatusV3;
    }
}

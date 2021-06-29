package com.lulobank.credits.v3.mapper;

import com.lulobank.credits.v3.service.LoanTransaction;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.foundCreditsEntityInBD;
import static com.lulobank.credits.v3.util.EntitiesFactory.LoanFactory.createLoanResponse;
import static com.lulobank.credits.v3.util.EntitiesFactory.PromissoryNodeFactoryTest.createPromissoryNoteResponse;
import static com.lulobank.credits.v3.util.EntitiesFactory.SavingsAccountFactory.createSavingsAccountResponse;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreditsV3EntityMapperTest {

    @Test
    public void MapperWithDayOfPay5() {
        LoanTransaction loanTransaction = getLoanTransaction();
        loanTransaction.getEntity().setDayOfPay(5);
        LoanTransaction loanTransactionResponse = CreditsV3EntityMapper.mapLoanInformation(loanTransaction);
        assertThat(loanTransactionResponse.getEntity().getStatementsIndex(), CoreMatchers.is("25#APPROVED"));
        assertThat(loanTransactionResponse.getEntity().getLoanStatus().getStatus(), CoreMatchers.is(loanTransaction.getLoanResponse().getAccountState()));
    }

    @Test
    public void MapperWithDayOfPay15() {
        LoanTransaction loanTransaction = getLoanTransaction();
        loanTransaction.getEntity().setDayOfPay(15);
        LoanTransaction loanTransactionResponse = CreditsV3EntityMapper.mapLoanInformation(loanTransaction);
        assertThat(loanTransactionResponse.getEntity().getStatementsIndex(), CoreMatchers.is("5#APPROVED"));
        assertThat(loanTransactionResponse.getEntity().getLoanStatus().getStatus(), CoreMatchers.is(loanTransaction.getLoanResponse().getAccountState()));
    }

    @Test
    public void MapperWithDayOfPay25() {
        LoanTransaction loanTransaction = getLoanTransaction();
        loanTransaction.getEntity().setDayOfPay(25);
        LoanTransaction loanTransactionResponse = CreditsV3EntityMapper.mapLoanInformation(loanTransaction);
        assertThat(loanTransactionResponse.getEntity().getStatementsIndex(), CoreMatchers.is("15#APPROVED"));
        assertThat(loanTransactionResponse.getEntity().getLoanStatus().getStatus(), CoreMatchers.is(loanTransaction.getLoanResponse().getAccountState()));
    }

    private LoanTransaction getLoanTransaction() {
        LoanTransaction loanTransaction = new LoanTransaction();
        loanTransaction.setSavingsAccountResponse(createSavingsAccountResponse());
        loanTransaction.setPromissoryNoteResponse(createPromissoryNoteResponse());
        loanTransaction.setCreditsV3Entity(foundCreditsEntityInBD());
        loanTransaction.setLoanResponse(createLoanResponse());
        return loanTransaction;
    }
}

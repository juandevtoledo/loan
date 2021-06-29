package com.lulobank.credits.starter.v3.adapters.out.promissorynote;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.service.LoanTransaction;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PromissoryNoteSqsAdapterTest {

    private PromissoryNoteSqsAdapter promissoryNoteSqsAdapter;

    private SqsBraveTemplate sqsBraveTemplate;
    @Before
    public void setUp() throws Exception {
        sqsBraveTemplate = mock(SqsBraveTemplate.class);
        promissoryNoteSqsAdapter = new PromissoryNoteSqsAdapter(sqsBraveTemplate, "promissory-note");
    }

    @Test
    public void createPromissoryNote() {
        LoanTransaction loanTransaction = new LoanTransaction();
        CreditsV3Entity entity = new CreditsV3Entity();
        entity.setIdCredit(UUID.randomUUID());
        loanTransaction.setCreditsV3Entity(entity);
        AcceptOffer acceptOffer = new AcceptOffer();
        promissoryNoteSqsAdapter.createPromissoryNote(loanTransaction, acceptOffer);
        verify(sqsBraveTemplate, times(1)).convertAndSend(Mockito.anyString(), Mockito.any());
    }
}
package com.lulobank.credits.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.service.LoanTransaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.lulobank.credits.starter.utils.Samples.loanTransactionBuilder;
import static com.lulobank.credits.starter.utils.Samples.savingsAccountResponseBuilder;
import static org.mockito.Mockito.verify;

public class QueueServiceAdapterTest {

    private QueueServiceAdapter queueServiceAdapter;
    private ClientCBSCreatedEvent clientCBSCreatedEvent;
    private SendSignedDocumentGroupSqs sendSignedDocumentGroupSqs;
    private PersistLoanDocumentEvent persistLoanDocumentEvent;
    private RiskEngineResultEventV2Event riskEngineResultEventV2Event;

    @Mock
    private SqsBraveTemplate sqsTemplate;

    private ApprovedLoanDocumentEvent approvedLoanDocumentEvent;
    @Captor
    private ArgumentCaptor<LoanTransaction> loanCaptorCbsCreated;
    @Captor
    private ArgumentCaptor<LoanTransaction> loanCaptorReporting;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        clientCBSCreatedEvent = new ClientCBSCreatedEvent("");
        persistLoanDocumentEvent = new PersistLoanDocumentEvent("");
        approvedLoanDocumentEvent = new ApprovedLoanDocumentEvent("");
        sendSignedDocumentGroupSqs = new SendSignedDocumentGroupSqs("");
        riskEngineResultEventV2Event = new RiskEngineResultEventV2Event("");

        queueServiceAdapter = new QueueServiceAdapter(clientCBSCreatedEvent, persistLoanDocumentEvent,
                sqsTemplate, approvedLoanDocumentEvent, sendSignedDocumentGroupSqs,riskEngineResultEventV2Event);
    }

    @Ignore
    @Test
    public void emitAcceptOfferNotification() {
        HashMap header = new HashMap();
        header.put("Authorization", "key-uath");
        LoanTransaction loanTransaction = loanTransactionBuilder(savingsAccountResponseBuilder());
        queueServiceAdapter.loanCreatedNotification(loanTransaction);
        verify(sqsTemplate, Mockito.atLeastOnce()).convertAndSend(Mockito.anyString(), Mockito.any(), Mockito.anyMap());
    }

}

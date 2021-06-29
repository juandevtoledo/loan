package com.lulobank.credits.starter.v3.adapters.in.sqs;

import co.com.lulobank.tracing.sqs.EventMessage;
import com.lulobank.credits.starter.v3.adapters.in.sqs.handler.*;
import com.lulobank.credits.starter.v3.adapters.out.clientalerts.ClientAlertsAdapterConfig;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseUseCase;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResultEventV2UseCase;
import com.lulobank.credits.v3.port.in.approvedriskengine.LoanAssessmentResultUseCase;
import com.lulobank.credits.v3.usecase.automaticdebit.MakeAutomaticPaymentUseCase;
import com.lulobank.credits.v3.port.in.clientinformation.UpdateProductEmailUseCase;
import com.lulobank.credits.v3.port.in.digitalevidence.DigitalEvidenceCreatedUseCase;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteCreatedUseCase;
import com.lulobank.credits.v3.port.in.rescheduledloan.RescheduledLoanUseCase;
import com.lulobank.credits.v3.usecase.IntentionLoanStatementUseCase;
import com.lulobank.credits.v3.usecase.closeloan.CloseLoanByExternalPaymentUseCase;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ClientAlertsAdapterConfig.class)
public class SqsRegistryHandlerConfig {

    @EventMessage(name = "UpdateProductEmailMessage")
    public UpdateProductEmailEventHandler getUpdateProductEmailEventHandler(UpdateProductEmailUseCase updateProductEmailUseCase) {
        return new UpdateProductEmailEventHandler(updateProductEmailUseCase);
    }

    @EventMessage(name = "DigitalEvidenceCreatedMessage")
    public DigitalEvidenceCreatedEventHandler getDigitalEvidenceCreatedEventHandler(DigitalEvidenceCreatedUseCase digitalEvidenceCreatedUseCase) {
        return new DigitalEvidenceCreatedEventHandler(digitalEvidenceCreatedUseCase);
    }

    @EventMessage(name = "CreatePromissoryNoteResponseMessage")
    public PromissoryNoteCreatedEventHandler getPromissoryNoteCreatedEventHandler(PromissoryNoteCreatedUseCase promissoryNoteCreatedUseCase) {
        return new PromissoryNoteCreatedEventHandler(promissoryNoteCreatedUseCase);
    }

    @EventMessage(name = "LoanStatementsScheduler")
    public LoanStatementSchedulerEventHandler getLoanStatementSchedulerEventHandler(IntentionLoanStatementUseCase intentionLoanStatementUseCase) {
        return new LoanStatementSchedulerEventHandler(intentionLoanStatementUseCase);
    }
    
    @EventMessage(name = "RiskEngineResponseMessage")
    public RiskEngineResponseEventHandler getRiskEngineResponseEventHandler(RiskEngineResponseUseCase riskEngineResponseUseCase) {
    	return new RiskEngineResponseEventHandler(riskEngineResponseUseCase);
    }

    @EventMessage(name = "RiskEngineResultEventV2Message")
    public RiskEngineResultEventV2Handler getRiskEngineResultEventV2Handler(RiskEngineResultEventV2UseCase riskEngineResultEventv2UseCase) {
        return new RiskEngineResultEventV2Handler(riskEngineResultEventv2UseCase);
    }
    
    @EventMessage(name = "LoanAssessmentResult")
    public LoanAssessmentResultHandler getLoanAssessmentResultHandler(LoanAssessmentResultUseCase loanAssessmentResultUseCase) {
        return new LoanAssessmentResultHandler(loanAssessmentResultUseCase);
    }

    @EventMessage(name = "RescheduledLoanMessage")
    public RescheduledLoanEventHandler getRescheduledLoanEventHandler(RescheduledLoanUseCase rescheduledLoanUseCase) {
        return new RescheduledLoanEventHandler(rescheduledLoanUseCase);
    }

    @EventMessage(name = "AutomaticDebitMessage")
    public AutomaticDebitMessageHandler automaticDebitMessageHandler(MakeAutomaticPaymentUseCase makeAutomaticPaymentUseCase) {
        return new AutomaticDebitMessageHandler(makeAutomaticPaymentUseCase);
    }

    @EventMessage(name = "CloseLoanByPSETotalPaymentMessage")
    public CloseLoanByPSEHandler closeLoanByPSEHandler(CloseLoanByExternalPaymentUseCase closeLoanByExternalPaymentUseCase) {
        return new CloseLoanByPSEHandler(closeLoanByExternalPaymentUseCase);
    }
}

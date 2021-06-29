package com.lulobank.credits.v3.service;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.ReportingQueueService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.mapper.CloseLoanServiceMapper;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;
import static com.lulobank.credits.v3.vo.CreditsError.persistError;

@CustomLog
@RequiredArgsConstructor
public class CloseLoanService {

    private final CreditsV3Repository creditsV3Repository;
    private final ReportingQueueService reportingQueueService;
    private final SchedulerTransactionAsyncService schedulerAsyncService;

    public Either<UseCaseResponseError, CreditsV3Entity> close(String idCredit, String idLoanCbs) {
        return creditsV3Repository.findByIdCreditAndIdLoanAccountMambu(idCredit, idLoanCbs)
                .toEither(CreditsError.idCreditNotFound())
                .map(CloseLoanServiceMapper.INSTANCE::loanStatusClosed)
                .flatMap(this::persistThenSendNotifications)
                .peek(entity -> log.info("success close loan, idCredit {} , idClient: {} ", idCredit, entity.getIdClient()))
                .peekLeft(error -> log.info("Error close loan, idCredit {} ,  ProviderCode: {} , businessCode : {}  ", idCredit, error.getProviderCode(), error.getBusinessCode()))
                .mapLeft(Function.identity());
    }

    private Either<CreditsError, CreditsV3Entity> persistThenSendNotifications(CreditsV3Entity creditsV3Entity) {
        return creditsV3Repository.save(creditsV3Entity)
                .toEither(persistError())
                .peek(success -> reportingQueueService.sendGoodStanding(CloseLoanServiceMapper.INSTANCE.goodStandingCertificateEvent(creditsV3Entity)))
                .peek(success -> schedulerAsyncService.deleteTransaction(transactionRequest(creditsV3Entity)))
                .map(success -> creditsV3Entity);
    }

}

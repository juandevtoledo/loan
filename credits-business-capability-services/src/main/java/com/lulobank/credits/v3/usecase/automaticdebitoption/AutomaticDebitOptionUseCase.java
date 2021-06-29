package com.lulobank.credits.v3.usecase.automaticdebitoption;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.usecase.automaticdebitoption.dto.UpdateAutomaticDebitOption;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.createTransactionRequest;
import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;
import static com.lulobank.credits.v3.vo.CreditsError.persistError;

@CustomLog
@RequiredArgsConstructor
public class AutomaticDebitOptionUseCase implements UseCase<UpdateAutomaticDebitOption, Either<UseCaseResponseError, Boolean>> {

    private final CreditsV3Repository creditsV3Repository;
    private final SchedulerTransactionAsyncService schedulerTransactionAsyncService;


    @Override
    public Either<UseCaseResponseError, Boolean> execute(UpdateAutomaticDebitOption command) {
        log.info("Updating automatic debit option, idClient: {}, option: {}", command.getIdClient(),
                command.getAutomaticDebit());

        return findCredit(command.getIdClient())
                .flatMap(credit -> updateAutomaticDebitOption(credit, command.getAutomaticDebit()))
                .peek(this::schedulerNotification)
                .peek(credit -> log.info("Automatic debit option updated successfully, idClient: {}",
                        command.getIdClient()))
                .map(credit -> true);
    }

    private Either<UseCaseResponseError, CreditsV3Entity> findCredit(String idClient) {
        return creditsV3Repository.findLoanActiveByIdClient(idClient)
                .onEmpty(() -> log.error("Credit not found, idClient: {}", idClient))
                .toEither(CreditsError::databaseError);
    }

    private Either<UseCaseResponseError, CreditsV3Entity> updateAutomaticDebitOption(CreditsV3Entity credit,
                                                                                     Boolean option) {
        credit.setAutomaticDebit(option);
        return creditsV3Repository.save(credit)
                .onFailure(error -> log.error("Error saving credit, idClient: {}, message : {}",
                        credit.getIdClient(), error.getMessage(), error))
                .toEither(persistError());
    }

    private void schedulerNotification(CreditsV3Entity credit) {
        if (Boolean.TRUE.equals(credit.getAutomaticDebit())) {
            schedulerTransactionAsyncService.createTransaction(createTransactionRequest(credit));
        } else {
            schedulerTransactionAsyncService.deleteTransaction(transactionRequest(credit));
        }
    }

}

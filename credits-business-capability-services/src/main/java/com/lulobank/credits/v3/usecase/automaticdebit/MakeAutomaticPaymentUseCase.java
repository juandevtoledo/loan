package com.lulobank.credits.v3.usecase.automaticdebit;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerNotificationAsyncService;
import com.lulobank.credits.v3.port.out.scheduler.automaticdebit.SchedulerTransactionAsyncService;
import com.lulobank.credits.v3.service.AutomaticDebitPaymentService;
import com.lulobank.credits.v3.usecase.automaticdebit.command.MakeAutomaticDebitCommand;
import com.lulobank.credits.v3.usecase.automaticdebit.command.ProcessPayment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import static com.lulobank.credits.v3.port.out.scheduler.automaticdebit.mapper.SchedulerAutomaticDebitMapper.transactionRequest;


@CustomLog
@RequiredArgsConstructor
public class MakeAutomaticPaymentUseCase implements UseCase<MakeAutomaticDebitCommand, Try<Void>> {

    private final CreditsV3Repository creditsV3Repository;
    private final AutomaticDebitPaymentService automaticDebitPaymentService;
    private final CoreBankingService coreBankingService;
    private final SchedulerTransactionAsyncService schedulerTransactionAsyncService;
    private final SchedulerNotificationAsyncService schedulerNotificationAsyncService;

    @Override
    public Try<Void> execute(MakeAutomaticDebitCommand command) {
        return Try.run(() -> makePayment(command))
                .onFailure(error -> log.error("AutomaticPayment process failed, msg {} ", error.getMessage(), error))
                .onSuccess(success -> log.info("success AutomaticPayment process by idCredit {} ", command.getIdCredit()));
    }

    private Either<UseCaseResponseError, LoanInformation> makePayment(MakeAutomaticDebitCommand command) {
        return creditsV3Repository.findById(command.getIdCredit())
                .toEither(CreditsError::idCreditNotFound)
                .flatMap(creditsV3Entity ->
                        coreBankingService.getLoanInformation(creditsV3Entity.getIdLoanAccountMambu(), creditsV3Entity.getIdClientMambu())
                                .peekLeft(coreBankingError -> schedulerTransactionAsyncService.retryTransaction(transactionRequest(creditsV3Entity,command.getMetadata())))
                                .peek(loanInformation -> paidDependingOnState(creditsV3Entity, loanInformation, command))
                                .mapLeft(CreditsError::toCreditError)
                ).peekLeft(error -> log.error("Automatic payment failed, providerCode : {} , businessCode : {} , detail : {}  ", error.getProviderCode(), error.getBusinessCode()))
                .mapLeft(Function.identity());
    }

    private void paidDependingOnState(CreditsV3Entity creditsV3Entity, LoanInformation loanInformation, MakeAutomaticDebitCommand command) {
        PaidState.find(loanInformation)
                .action(getProcessPayment(creditsV3Entity, loanInformation, command),
                        automaticDebitPaymentService, schedulerTransactionAsyncService, schedulerNotificationAsyncService);
    }

    private ProcessPayment getProcessPayment(CreditsV3Entity creditsV3Entity, LoanInformation loanInformation, MakeAutomaticDebitCommand command) {
        return ProcessPayment.builder()
                .loanInformation(loanInformation)
                .dayOfPay(creditsV3Entity.getDayOfPay())
                .idClient(creditsV3Entity.getIdClient())
                .idCredit(creditsV3Entity.getIdCredit().toString())
                .idLoanAccountMambu(creditsV3Entity.getIdLoanAccountMambu())
                .idCoreBanking(creditsV3Entity.getIdClientMambu())
                .metadataEvent(command.getMetadata())
                .build();
    }
}

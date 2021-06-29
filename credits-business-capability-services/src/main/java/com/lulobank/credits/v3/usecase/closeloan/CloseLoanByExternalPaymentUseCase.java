package com.lulobank.credits.v3.usecase.closeloan;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.queue.PseAsyncService;
import com.lulobank.credits.v3.service.CloseLoanService;
import com.lulobank.credits.v3.usecase.closeloan.command.ClientWithExternalPayment;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@CustomLog
@RequiredArgsConstructor
public class CloseLoanByExternalPaymentUseCase implements UseCase<ClientWithExternalPayment, Either<UseCaseResponseError, String>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CloseLoanService closeLoanService;
    private final PseAsyncService pseAsyncService;

    @Override
    public Either<UseCaseResponseError, String> execute(ClientWithExternalPayment command) {

        return creditsV3Repository.findLoanActiveByIdClient(command.getIdClient())
                .toEither(CreditsError::idCreditNotFound)
                .peekLeft(notFound->log.info("Credit already is not active , by idClient {} ",command.getIdClient()))
                .fold(error -> Either.right(command.getIdClient()), entity->close(entity,command));
    }

    private Either<UseCaseResponseError, String> close(CreditsV3Entity creditsV3Entity, ClientWithExternalPayment command) {
        return closeLoanService.close(creditsV3Entity.getIdCredit().toString(), creditsV3Entity.getIdLoanAccountMambu())
                .map(CreditsV3Entity::getIdClient)
                .peek(entity->pseAsyncService.loanClosed(command.getIdClient(),command.getProductTransaction()));
    }

}

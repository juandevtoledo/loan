package com.lulobank.credits.v3.usecase.paymentplan;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.usecase.paymentplan.mapper.LoanPaymentPlanUseCaseMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import java.util.function.Function;

@CustomLog
@AllArgsConstructor
public class LoanPaymentPlantUseCase implements UseCase<String, Either<UseCaseResponseError, PaymentPlanUseCaseResponse>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CoreBankingService coreBankingService;

    @Override
    public Either<UseCaseResponseError, PaymentPlanUseCaseResponse> execute(String idClient) {
        log.info("Getting payment plan for active loan, idClient: {}", idClient);
        return creditsV3Repository.findLoanActiveByIdClient(idClient)
                .onEmpty(() -> log.error("Credit not found, idClient: {}", idClient))
                .toEither(CreditsError::databaseError)
                .flatMap(this::getPaymentPlan)
                .peek(ignored -> log.info("Payment plan generated successfully, idClient: {}", idClient))
                .mapLeft(Function.identity());
    }

    private Either<CreditsError, PaymentPlanUseCaseResponse> getPaymentPlan(CreditsV3Entity credit) {
        return coreBankingService.getLoanInformation(credit.getIdLoanAccountMambu(), credit.getIdClientMambu())
                .peekLeft(error -> log.info("Loan information not found, idClient: {}", credit.getIdClient(),
                        error))
                .map(LoanPaymentPlanUseCaseMapper::paymentPlanUseCaseResponseMapperFrom)
                .mapLeft(CreditsError::toCreditError);
    }
}

package com.lulobank.credits.v3.usecase.nextinstallment;

import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.usecase.nextinstallment.mapper.NextInstallmentMapper;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class NextInstallmentUseCase implements GenerateNextInstallmentPort {

    private final CoreBankingService coreBankingService;
    private final CreditsV3Repository creditsV3Repository;

    @Override
    public Either<UseCaseResponseError, NextInstallment> execute(String command) {
        return creditsV3Repository.findLoanActiveByIdClient(command)
                .toEither(CreditsError::databaseError)
                .flatMap(this::getNextInstallmentByLoanInformation)
                .mapLeft(Function.identity());
    }

    private Either<CreditsError, NextInstallment> getNextInstallmentByLoanInformation(CreditsV3Entity creditsEntity) {
        return coreBankingService.getLoanInformation(creditsEntity.getIdLoanAccountMambu(), creditsEntity.getIdClientMambu())
                .mapLeft(CreditsError::toCreditError)
                .map(loanInformation -> NextInstallmentMapper.INSTANCE.nextInstallmentTO(creditsEntity, loanInformation, InstallmentState.get(loanInformation)));
    }
}

package com.lulobank.credits.v3.usecase.installment;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.usecase.installment.command.CalculateExtraAmountInstallment;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import com.lulobank.credits.v3.usecase.installment.util.ExtraAmountType;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.ExtraAmountInstallmentError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;

import java.math.BigDecimal;

public class ExtraAmountInstallmentUseCase implements UseCase<CalculateExtraAmountInstallment, Either<UseCaseResponseError, ExtraAmountInstallmentResult>> {

    private final CoreBankingService coreBankingService;
    private final CreditsV3Repository creditsV3Repository;

    public ExtraAmountInstallmentUseCase(CoreBankingService coreBankingService, CreditsV3Repository creditsV3Repository) {
        this.coreBankingService = coreBankingService;
        this.creditsV3Repository = creditsV3Repository;
    }

    @Override
    public Either<UseCaseResponseError, ExtraAmountInstallmentResult> execute(CalculateExtraAmountInstallment command) {
        return creditsV3Repository.findById(command.getIdCredit())
                .toEither(CreditsError.databaseError())
                .mapLeft(UseCaseResponseError::map)
                .flatMap(creditsV3Entity -> coreBankingService.getLoanInformation(creditsV3Entity.getIdLoanAccountMambu(),
                        creditsV3Entity.getIdClientMambu())
                        .mapLeft(UseCaseResponseError::map))
                .flatMap(coreBankingInformation -> paymentProfiling(coreBankingInformation, command.getAmount()));
    }


    private Either<UseCaseResponseError, ExtraAmountInstallmentResult> paymentProfiling(LoanInformation loanInformation, BigDecimal amount) {

        return ExtraAmountType.get(loanInformation.getInstallmentExpected().getValue(),
                loanInformation.getTotalBalance().getValue(), amount)
                .toEither(ExtraAmountInstallmentError.errorExtraPayment());
    }

}

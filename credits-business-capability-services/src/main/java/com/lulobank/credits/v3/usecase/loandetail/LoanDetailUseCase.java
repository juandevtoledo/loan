package com.lulobank.credits.v3.usecase.loandetail;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.LoanInformation;
import com.lulobank.credits.v3.port.out.corebanking.dto.Movement;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import com.lulobank.credits.v3.usecase.loandetail.mapper.LoanDetailMapper;
import com.lulobank.credits.v3.usecase.movement.dto.MovementType;
import com.lulobank.credits.v3.usecase.movement.mapper.MovementMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@CustomLog
@RequiredArgsConstructor
public class LoanDetailUseCase implements UseCase<String, Either<UseCaseResponseError, LoanDetail>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CoreBankingService coreBankingService;

    @Override
    public Either<UseCaseResponseError, LoanDetail> execute(String command) {
        return creditsV3Repository.findLoanActiveByIdClient(command)
                .toEither(CreditsError::databaseError)
                .flatMap(this::getLoanDetail)
                .peekLeft(error -> log.error("Error building LoanDetail  msg : {} , idClient: {}  ", error.getBusinessCode(), error.getProviderCode(), command))
                .mapLeft(Function.identity());

    }

    private Either<CreditsError, LoanDetail> getLoanDetail(CreditsV3Entity creditsV3Entity) {
        return coreBankingService.getLoanInformation(creditsV3Entity.getIdLoanAccountMambu(), creditsV3Entity.getIdClientMambu())
                .mapLeft(CreditsError::toCreditError)
                .flatMap(loanInformation -> getMap(creditsV3Entity, loanInformation));
    }

    private Either<CreditsError, LoanDetail> getMap(CreditsV3Entity creditsV3Entity, LoanInformation loanInformation) {
        return coreBankingService.getLoanMovements(MovementMapper.INSTANCE.getMovementsRequestFrom(creditsV3Entity))
                .mapLeft(CreditsError::toCreditError)
                .map(this::sumPaidMovements)
                .map(paidAmount -> LoanDetailMapper.INSTANCE.loanDetailTo(creditsV3Entity, loanInformation, paidAmount));
    }

    private BigDecimal sumPaidMovements(List<Movement> movements) {
        return movements.stream()
                .filter(movement -> MovementType.isValidType(movement.getState()))
                .map(Movement::getTotalDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

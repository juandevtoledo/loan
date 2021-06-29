package com.lulobank.credits.v3.usecase.movement;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.port.out.corebanking.CoreBankingService;
import com.lulobank.credits.v3.port.out.corebanking.dto.GetMovementsRequest;
import com.lulobank.credits.v3.usecase.movement.dto.GetMovements;
import com.lulobank.credits.v3.usecase.movement.dto.Movement;
import com.lulobank.credits.v3.usecase.movement.dto.MovementType;
import com.lulobank.credits.v3.usecase.movement.mapper.MovementMapper;
import com.lulobank.credits.v3.util.UseCase;
import com.lulobank.credits.v3.vo.CreditsError;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@CustomLog
@AllArgsConstructor
public class LoanMovementsUseCase implements UseCase<GetMovements, Either<UseCaseResponseError, List<Movement>>> {

    private final CreditsV3Repository creditsV3Repository;
    private final CoreBankingService coreBankingService;

    @Override
    public Either<UseCaseResponseError, List<Movement>> execute(GetMovements command) {
        return creditsV3Repository.findLoanActiveByIdClient(command.getIdClient())
                .toEither(CreditsError::databaseError)
                .map(MovementMapper.INSTANCE::getMovementsRequestFrom)
                .flatMap(getMovementsReq -> getMovements(getMovementsReq, command.getOffset(), command.getLimit()))
                .peekLeft(error -> log.error("Error getting loan movements idClient: {}, msg : {}",
                        command.getIdClient(), error.getBusinessCode()))
                .mapLeft(Function.identity());
    }

    private Either<CreditsError, List<Movement>>  getMovements(GetMovementsRequest getMovementsRequest, Integer offset,
                                                               Integer limit)  {
        return coreBankingService.getLoanMovements(getMovementsRequest)
                .mapLeft(CreditsError::toCreditError)
                .map(MovementMapper.INSTANCE::movementsFrom)
                .map(movements -> movements.stream()
                    .filter(mov-> MovementType.isValidType(mov.getState()))
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList()));
    }
}

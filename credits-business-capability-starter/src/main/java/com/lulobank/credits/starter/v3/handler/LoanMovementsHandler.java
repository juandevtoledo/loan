package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.movement.MovementsResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.MovementMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.movement.LoanMovementsUseCase;
import com.lulobank.credits.v3.usecase.movement.dto.GetMovements;
import com.lulobank.credits.v3.usecase.movement.dto.Movement;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@CustomLog
@AllArgsConstructor
public class LoanMovementsHandler {

    private final LoanMovementsUseCase loanMovementsUseCase;

    public ResponseEntity<AdapterResponse> getLoanMovements(String idClient, Integer offset, Integer limit) {
        return loanMovementsUseCase.execute(GetMovements.builder().idClient(idClient).offset(offset)
                    .limit(limit).build())
                .fold(this::mapError, this::mapResponse);
    }

    private ResponseEntity<AdapterResponse> mapResponse(List<Movement> movements) {
        return AdapterResponseUtil.ok(MovementsResponse.builder()
                .movements(MovementMapper.INSTANCE.movementsFrom(movements))
                .build());
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

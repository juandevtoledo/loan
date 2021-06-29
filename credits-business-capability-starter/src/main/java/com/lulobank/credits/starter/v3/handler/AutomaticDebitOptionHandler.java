package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption.dto.UpdateAutomaticDebitOptiontRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.v3.usecase.automaticdebitoption.AutomaticDebitOptionUseCase;
import com.lulobank.credits.v3.usecase.automaticdebitoption.dto.UpdateAutomaticDebitOption;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.ok;

@CustomLog
@RequiredArgsConstructor
public class AutomaticDebitOptionHandler {

    private final AutomaticDebitOptionUseCase automaticDebitOptionUseCase;

    public ResponseEntity<AdapterResponse> execute(UpdateAutomaticDebitOptiontRequest request, String idClient) {
        return automaticDebitOptionUseCase.execute(new UpdateAutomaticDebitOption(
                    idClient, request.getAutomaticDebit()))
                .fold(this::mapError, success -> ok());
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

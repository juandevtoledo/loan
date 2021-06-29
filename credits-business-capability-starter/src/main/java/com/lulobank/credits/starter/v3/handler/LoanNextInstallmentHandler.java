package com.lulobank.credits.starter.v3.handler;


import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.NextInstallmentResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.port.in.nextinstallment.GenerateNextInstallmentPort;
import com.lulobank.credits.v3.port.in.nextinstallment.dto.NextInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@RequiredArgsConstructor
public class LoanNextInstallmentHandler {

    private final GenerateNextInstallmentPort generateNextInstallmentUseCase;

    public ResponseEntity<AdapterResponse> get(String idClient){
        return generateNextInstallmentUseCase.execute(idClient)
                .fold(this::mapError, this::mapResponse);
    }

    private ResponseEntity<AdapterResponse> mapResponse(NextInstallment nextInstallment) {
        return AdapterResponseUtil.ok(NextInstallmentResponseMapper.INSTANCE.nextInstallmentResponseTo(nextInstallment));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

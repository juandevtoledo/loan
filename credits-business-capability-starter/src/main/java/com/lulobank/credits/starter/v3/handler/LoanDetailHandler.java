package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.LoanInformationMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.loandetail.LoanDetailUseCase;
import com.lulobank.credits.v3.usecase.loandetail.dto.LoanDetail;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@RequiredArgsConstructor
public class LoanDetailHandler {

    private final LoanDetailUseCase loanDetailUseCase;

    public ResponseEntity<AdapterResponse> get(String idClient) {
        return loanDetailUseCase.execute(idClient)
                .fold(this::mapError, this::mapResponse);
    }

    private ResponseEntity<AdapterResponse> mapResponse(LoanDetail response) {
        return AdapterResponseUtil.ok(LoanInformationMapper.INSTANCE.loanDetailResponseTo(response));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

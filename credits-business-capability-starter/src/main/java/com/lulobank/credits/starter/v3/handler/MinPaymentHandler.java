package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.MinimumPaymentRequest;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.payment.MinimumPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.command.MinPaymentInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@CustomLog
@RequiredArgsConstructor
public class MinPaymentHandler {

    private final MinimumPaymentUseCase minimumPaymentUseCase;

    public ResponseEntity<AdapterResponse> makePayment(MinimumPaymentRequest minimumPaymentRequest, String idClient) {
        return minimumPaymentUseCase.execute(getPaymentInstallment(minimumPaymentRequest, idClient))
                .fold(this::mapError, success -> AdapterResponseUtil.accepted());
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

    private MinPaymentInstallment getPaymentInstallment(MinimumPaymentRequest minimumPaymentRequest, String idClient) {
        return MinPaymentInstallment.builder()
                .creditId(minimumPaymentRequest.getIdCredit())
                .clientId(idClient)
                .amount(minimumPaymentRequest.getAmount())
                .coreCbsId(minimumPaymentRequest.getIdCreditCBS())
                .build();
    }


}

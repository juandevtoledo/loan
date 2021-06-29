package com.lulobank.credits.starter.v3.handler;


import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.TotalPaymentRequest;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.payment.TotalPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.command.TotalPaymentInstallment;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@CustomLog
@RequiredArgsConstructor
public class TotalPaymentHandler {

    private final TotalPaymentUseCase totalPaymentUseCase;

    public ResponseEntity<AdapterResponse> makePayment(TotalPaymentRequest totalPaymentRequest, String idClient, HttpHeaders headers) {
        return totalPaymentUseCase.execute(getPaymentInstallment(totalPaymentRequest, idClient, headers))
                .fold(this::mapError, success -> AdapterResponseUtil.accepted());
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

    private TotalPaymentInstallment getPaymentInstallment(TotalPaymentRequest totalPaymentRequest, String idClient,HttpHeaders headers) {
        return TotalPaymentInstallment.builder()
                .creditId(totalPaymentRequest.getIdCredit())
                .clientId(idClient)
                .amount(totalPaymentRequest.getAmount())
                .coreCbsId(totalPaymentRequest.getIdCreditCBS())
                .adapterCredentials(new AdapterCredentials(headers.toSingleValueMap()))
                .build();
    }
}

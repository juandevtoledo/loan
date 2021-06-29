package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.CustomPaymentRequest;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.payment.CustomPaymentUseCase;
import com.lulobank.credits.v3.usecase.payment.command.CustomPaymentInstallment;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@RequiredArgsConstructor
public class CustomPaymentHandler {

    private final CustomPaymentUseCase customPaymentUseCase;

    public ResponseEntity<AdapterResponse> makePayment(CustomPaymentRequest customPaymentRequest, String idClient) {
        return customPaymentUseCase.execute(getPaymentCustomInstallment(customPaymentRequest, idClient))
                .fold(this::mapError, success -> AdapterResponseUtil.accepted());
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

    private CustomPaymentInstallment getPaymentCustomInstallment(CustomPaymentRequest customPaymentRequest, String idClient) {
        return CustomPaymentInstallment.builder()
                .creditId(customPaymentRequest.getIdCredit())
                .clientId(idClient)
                .amount(customPaymentRequest.getAmount())
                .coreCbsId(customPaymentRequest.getIdCreditCBS())
                .type(customPaymentRequest.getType())
                .build();
    }
}

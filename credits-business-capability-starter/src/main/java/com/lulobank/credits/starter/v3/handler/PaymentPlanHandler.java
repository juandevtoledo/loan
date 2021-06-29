package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanRequest;
import com.lulobank.credits.starter.v3.adapters.in.error.ResponseValidationError;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.PaymentPlanMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.usecase.paymentplan.command.GetPaymentPlan;
import com.lulobank.credits.v3.usecase.paymentplan.PaymentPlantUseCase;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.UUID;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@CustomLog
@AllArgsConstructor
public class PaymentPlanHandler {

    private final PaymentPlantUseCase paymentPlantUseCase;

    public ResponseEntity<AdapterResponse> getPaymentPlan(PaymentPlanRequest paymentPlanRequest, String idClient, BindingResult bindingResult) {

        return bindingResult.hasErrors() ?
                ResponseValidationError.of(bindingResult) :
                processUseCase(paymentPlanRequest, idClient);
    }

    private ResponseEntity<AdapterResponse> processUseCase(PaymentPlanRequest paymentPlanRequest, String idClient) {
        return paymentPlantUseCase.execute(getUseCaseRequest(paymentPlanRequest, idClient))
                .fold(this::mapError, this::mapResponse);
    }

    private GetPaymentPlan getUseCaseRequest(PaymentPlanRequest paymentPlanRequest, String idClient) {
        return GetPaymentPlan.builder()
                .idClient(idClient)
                .idCredit(UUID.fromString(paymentPlanRequest.getIdCredit()))
                .dayOfPay(paymentPlanRequest.getDayOfPay())
                .idOffer(paymentPlanRequest.getIdOffer())
                .installments(paymentPlanRequest.getInstallments())
                .build();
    }

    private ResponseEntity<AdapterResponse> mapResponse(PaymentPlanUseCaseResponse response) {
        return AdapterResponseUtil.ok(PaymentPlanMapper.INSTANCE.paymentPlanResponseFrom(response));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

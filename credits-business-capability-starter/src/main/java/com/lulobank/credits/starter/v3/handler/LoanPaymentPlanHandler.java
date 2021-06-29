package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.PaymentPlanMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.paymentplan.LoanPaymentPlantUseCase;
import com.lulobank.credits.v3.usecase.paymentplan.command.PaymentPlanUseCaseResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@AllArgsConstructor
public class LoanPaymentPlanHandler {

    private final LoanPaymentPlantUseCase loanPaymentPlantUseCase;

    public ResponseEntity<AdapterResponse> getPaymentPlan(String idClient) {
        return loanPaymentPlantUseCase.execute(idClient)
                .fold(this::mapError, this::mapResponse);
    }

    private ResponseEntity<AdapterResponse> mapResponse(PaymentPlanUseCaseResponse response) {
        return AdapterResponseUtil.ok(PaymentPlanMapper.INSTANCE.paymentPlanResponseFrom(response));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

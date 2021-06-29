package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.mappers.ExtraAmountMapper;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.installment.ExtraAmountInstallmentUseCase;
import com.lulobank.credits.v3.usecase.installment.command.CalculateExtraAmountInstallment;
import com.lulobank.credits.v3.usecase.installment.dto.ExtraAmountInstallmentResult;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

public class ExtraAmountHandler {

    private final ExtraAmountInstallmentUseCase extraAmountInstallmentUseCase;

    public ExtraAmountHandler(ExtraAmountInstallmentUseCase extraAmountInstallmentUseCase) {
        this.extraAmountInstallmentUseCase = extraAmountInstallmentUseCase;
    }

    public ResponseEntity<AdapterResponse> executeUseCase(String idCredit, BigDecimal amount){
        return extraAmountInstallmentUseCase.execute(CalculateExtraAmountInstallment.builder().idCredit(idCredit).amount(amount).build())
                .fold(this::mapError, this::mapResponse);
    }


    private ResponseEntity<AdapterResponse> mapResponse(ExtraAmountInstallmentResult response) {
        return AdapterResponseUtil.ok(ExtraAmountMapper.INSTANCE.toExtraAmountResponse(response));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }
}

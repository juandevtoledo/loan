package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.sdk.dto.paymentplantv3.PaymentPlanRequestV3;
import com.lulobank.credits.starter.v3.mappers.PaymentPlanMapperV3;
import com.lulobank.credits.starter.v3.util.Messages;
import com.lulobank.credits.starter.v3.util.ResponseUtil;
import com.lulobank.credits.v3.dto.ErrorUseCaseV3;
import com.lulobank.credits.v3.usecase.PaymentPlantV3UseCase;
import io.vavr.control.Option;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
@RestController
@CrossOrigin(origins = "*")
@CustomLog
public class PaymentPlanAdapterV3 {

    private PaymentPlantV3UseCase paymentPlantV3UseCase;

    @Autowired
    public PaymentPlanAdapterV3(PaymentPlantV3UseCase paymentPlantV3UseCase) {
        this.paymentPlantV3UseCase = paymentPlantV3UseCase;
    }

    @PostMapping(value = "/v4/client/{idClient}/payment-plan")
    public ResponseEntity getPaymentPlanV4(@RequestHeader final HttpHeaders headers,
                                           @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                           @Valid @RequestBody final PaymentPlanRequestV3 paymentPlanRequestV3,
                                           BindingResult bindingResult) {

        return Option.of(bindingResult)
                .filter(Errors::hasErrors)
                .map(ResponseUtil::of)
                .getOrElse(processV4(paymentPlanRequestV3, idClient));
    }

    private Supplier<ResponseEntity> processV4(PaymentPlanRequestV3 paymentPlanRequestV3, String idClient) {
        return () -> {
            paymentPlanRequestV3.setIdClient(idClient);
            return paymentPlantV3UseCase.execute(PaymentPlanMapperV3.INSTANCE.getPaymentPlanFrom(paymentPlanRequestV3))
                    .fold(errorUsesCase(), paymentPlanV3s ->
                            ResponseUtil.buildResponse(PaymentPlanMapperV3.INSTANCE.paymentPlanResponseV4From(paymentPlanV3s), HttpStatus.OK)
                    );
        };

    }

    private Function<ErrorUseCaseV3, ResponseEntity<Object>> errorUsesCase() {
        return error ->
                ResponseUtil.buildErrorResponse(PaymentPlanMapperV3.INSTANCE.errorResultV3From(error), error.getCode());
    }
}

package com.lulobank.credits.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaid;
import com.lulobank.credits.sdk.dto.payment.InstallmentPaidResponse;
import com.lulobank.credits.sdk.dto.payment.PaymentInstallment;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.lulobank.credits.services.utils.HttpCodes.NOT_ACCEPTABLE;
import static com.lulobank.credits.services.utils.ResponseUtils.getResponseBindingResult;
import static com.lulobank.credits.services.utils.ResponseUtils.getResponseEntityByStatus;
import static com.lulobank.credits.services.utils.ResponseUtils.getResponseEntityError;

@RefreshScope
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/loan")
public class PaymentCreditAdapter {

    @Autowired
    @Qualifier("paymentInstallmentDecoratorHandler")
    private PostActionsDecoratorHandler paymentInstallmentDecoratorHandler;

    @PostMapping(value = "/client/{idClient}/payment/installment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Pay installment", notes = "Pay next installment for the given loan")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Make payment successfully",
                    response = Response.class),
            @ApiResponse(code = 403, message = "Forbidden client error"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 406, message = "Unexpected errors | Core banking errors")})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Deprecated
    public ResponseEntity payInstallment(@RequestHeader final HttpHeaders headers,
                                         @Valid @RequestBody final PaymentInstallment request,
                                         @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                         BindingResult bindingResult) {
        setRequestFields(request, headers, idClient);
        return bindingResult.hasErrors() ? getResponseEntityError(getResponseBindingResult(bindingResult)) :
                getResponseEntityFromHandler(request);
    }

    private void setRequestFields(PaymentInstallment request, HttpHeaders headers, String idClient) {
        request.setHttpHeaders(headers.toSingleValueMap());
        request.setIdClient(idClient);
    }

    @NotNull
    private ResponseEntity getResponseEntityFromHandler(PaymentInstallment request) {
        Response<InstallmentPaidResponse> response = paymentInstallmentDecoratorHandler.handle(request);
        ResponseEntity responseEntity;
        if (Boolean.TRUE.equals(response.getHasErrors())) {
            responseEntity = getResponseEntityError(response);
        } else {
            responseEntity = (response.getContent() instanceof InstallmentPaid) ? ResponseEntity.accepted().build() :
                    getResponseEntityByStatus(response, NOT_ACCEPTABLE);
        }
        return responseEntity;
    }
}
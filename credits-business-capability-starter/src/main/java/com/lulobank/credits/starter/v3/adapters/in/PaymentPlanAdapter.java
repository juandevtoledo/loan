package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.PaymentPlanResponse;
import com.lulobank.credits.starter.v3.handler.LoanPaymentPlanHandler;
import com.lulobank.credits.starter.v3.handler.PaymentPlanHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@CustomLog
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/client/{idClient}/")
@RequiredArgsConstructor
public class PaymentPlanAdapter {

    @Autowired
    private final PaymentPlanHandler paymentPlanHandler;
    @Autowired
    private final LoanPaymentPlanHandler loanPaymentPlanHandler;

    @PostMapping(value = "payment-plan", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get payment plan", notes = "Get payment plan for the given client and loan offer.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get payment plan successfully",
                    response = PaymentPlanResponse.class),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Error getting info from database")})
    public ResponseEntity<AdapterResponse> getPaymentPlan(@PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                                          @Valid @RequestBody final PaymentPlanRequest paymentPlanRequest,
                                                          BindingResult bindingResult) {
        return paymentPlanHandler.getPaymentPlan(paymentPlanRequest, idClient, bindingResult);
    }

    @GetMapping(value = "payment-plan", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get payment plan", notes = "Returns payment plan for the active credit of the given client.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get payment plan successfully",
                    response = PaymentPlanResponse.class),
            @ApiResponse(code = 400, message = "Any validation error"),
            @ApiResponse(code = 404, message = "Credit not found")})
    public ResponseEntity<AdapterResponse> getPaymentPlanActiveLoan(@PathVariable("idClient") String idClient) {
        return loanPaymentPlanHandler.getPaymentPlan(idClient);
    }
}

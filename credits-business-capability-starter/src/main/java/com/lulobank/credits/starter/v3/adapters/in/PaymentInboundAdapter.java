package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.CustomPaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.MinimumPaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.TotalPaymentRequest;
import com.lulobank.credits.starter.v3.handler.CustomPaymentHandler;
import com.lulobank.credits.starter.v3.handler.MinPaymentHandler;
import com.lulobank.credits.starter.v3.handler.TotalPaymentHandler;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.collection.Iterator;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_REQUEST;
import static com.lulobank.credits.v3.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_100;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_104;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.UNKNOWN_DETAIL;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.VALIDATION_DETAIL;

@CustomLog
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/client/{idClient}/payment")
@AllArgsConstructor
public class PaymentInboundAdapter {

    @Autowired
    private final MinPaymentHandler minPaymentHandler;
    @Autowired
    private final CustomPaymentHandler customPaymentHandler;
    @Autowired
    private final TotalPaymentHandler totalPaymentHandler;

    @PostMapping(value = "/minimum", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make minimum payment", notes = "Make a minimum payment for the given loan and the " +
            "specified amount.")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Make payment successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 502, message = "Account blocked")})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AdapterResponse> minimumPayment(@RequestHeader final HttpHeaders headers,
                                                          @PathVariable("idClient") String idClient,
                                                          @Valid @RequestBody final MinimumPaymentRequest request) {

        return minPaymentHandler.makePayment(request, idClient);
    }

    @PostMapping(value = "/custom", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make custom payment", notes = "Make a custom payment for the given loan and the " +
            "specified amount. Custom payments must provide a type (AMOUNT_INSTALLMENTS | NUMBER_INSTALLMENTS) to " +
            "reduce the current amount of the installment or the number of installments.")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Make payment successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 502, message = "Account blocked")})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AdapterResponse> customPayment(@RequestHeader final HttpHeaders headers,
                                                         @PathVariable("idClient") String idClient,
                                                         @Valid @RequestBody final CustomPaymentRequest request) {

        return customPaymentHandler.makePayment(request, idClient);
    }

    @PostMapping(value = "/total", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make total payment", notes = "Make total payment for the given loan. After a successful " +
            "payment the loan is closed and a good standing report is sent.")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Make payment successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 502, message = "Account blocked | idCredit not found | Error in persist information " +
                    "in database")})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AdapterResponse> totalPayment(@RequestHeader final HttpHeaders headers,
                                                        @PathVariable("idClient") String idClient,
                                                        @Valid @RequestBody final TotalPaymentRequest request) {

        return totalPaymentHandler.makePayment(request, idClient, headers);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return Iterator.ofAll(ex.getBindingResult().getAllErrors())
                .peek(error -> log.error("Validation error: {}", error.getDefaultMessage()))
                .map(error -> new ErrorResponse(String.valueOf(BAD_REQUEST.value()), CRE_104.name(), VALIDATION_DETAIL))
                .getOrElse(new ErrorResponse(String.valueOf(INTERNAL_SERVER_ERROR.value()), CRE_100.name(), UNKNOWN_DETAIL));
    }
}

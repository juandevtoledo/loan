package com.lulobank.credits.starter.v3.adapters.in.payment;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentRequest;
import com.lulobank.credits.starter.v3.adapters.in.payment.dto.PaymentResponse;
import com.lulobank.credits.starter.v3.handler.PaymentHandler;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.collection.Iterator;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RestController("LoanPaymentInboundAdapter")
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/loan/client/{idClient}/payment/account")
@AllArgsConstructor
public class PaymentInboundAdapter {

    @Autowired
    private final PaymentHandler loanPaymentHandler;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Make payment from account", notes = "Make a loan payment using client account balance. " +
            "The payment type can be MINIMUM_PAYMENT, TOTAL_PAYMENT, EXTRA_AMOUNT_PAYMENT, " +
            "MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS. In case of EXTRA_AMOUNT_PAYMENT or MINIMUM_AND_EXTRA_AMOUNT_PAYMENTS " +
            "payments is mandatory define a subPaymentType (AMOUNT_INSTALLMENT | NUMBER_INSTALLMENT) to reduce " +
            "the current amount of the installment or the number of installments. After a successful payment, if the " +
            "payment type is TOTAL_PAYMENT the loan will be closed and a good standing report will be sent.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Make payment successfully", response = PaymentResponse.class),
            @ApiResponse(code = 400, message = "Validation error")})
    public ResponseEntity<AdapterResponse> makePayment(@PathVariable("idClient") String idClient,
                                                       @Valid @RequestBody final PaymentRequest request) {
        return loanPaymentHandler.makePayment(request, idClient);
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

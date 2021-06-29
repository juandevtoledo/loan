package com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption;

import com.lulobank.credits.starter.v3.adapters.in.automaticdebitoption.dto.UpdateAutomaticDebitOptiontRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.handler.AutomaticDebitOptionHandler;
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
import org.springframework.web.bind.annotation.PutMapping;
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
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/loan/client/{idClient}/")
@AllArgsConstructor
public class AutomaticDebitOptionAdapter {

    @Autowired
    private final AutomaticDebitOptionHandler automaticDebitOptionHandler;

    @PutMapping(value = "automatic-debit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update automatic debit option", notes = "Update automatic debit option for the active " +
            "loan of the given client.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update debit option successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Credit not found"),
            @ApiResponse(code = 502, message = "Error in persist information in database")})
    public ResponseEntity<AdapterResponse> updateAutomaticDebitOption(@PathVariable("idClient") String idClient,
            @Valid @RequestBody final UpdateAutomaticDebitOptiontRequest request) {
        return automaticDebitOptionHandler.execute(request, idClient);
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

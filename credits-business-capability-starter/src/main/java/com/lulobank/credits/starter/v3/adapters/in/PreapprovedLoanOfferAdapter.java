package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersResponse;
import com.lulobank.credits.starter.v3.handler.PreapprovedLoanOfferHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.collection.Iterator;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
import javax.validation.constraints.NotBlank;

import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_REQUEST;
import static com.lulobank.credits.v3.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_100;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.CRE_104;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.UNKNOWN_DETAIL;
import static com.lulobank.credits.v3.vo.CreditsErrorStatus.VALIDATION_DETAIL;

@CustomLog
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/loan/pre-approved/client/{idClient}/")
@RequiredArgsConstructor
public class PreapprovedLoanOfferAdapter {

    @Autowired
    private final PreapprovedLoanOfferHandler preapprovedLoanOfferHandler;



    @PostMapping(value = "offer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Generate offer", notes = "Generate pre-approved offer for the given client and requested" +
            "amount.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Generate offer successfully",
                    response = PreapprovedLoanOffersResponse.class),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Error getting info from database"),
            @ApiResponse(code = 406, message = "Error generating offer"),
            @ApiResponse(code = 502, message = "Error in persist information in database")})
    public ResponseEntity<AdapterResponse> generateOffers(@RequestHeader final HttpHeaders headers,
                                                          @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                                          @Valid @RequestBody final PreapprovedLoanOffersRequest preapprovedLoanOffersRequest,
                                                          BindingResult bindingResult) {

        return preapprovedLoanOfferHandler.generateProductOffer(preapprovedLoanOffersRequest,idClient);

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

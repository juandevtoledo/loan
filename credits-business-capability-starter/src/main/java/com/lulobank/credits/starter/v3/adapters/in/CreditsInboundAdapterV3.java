package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.mappers.CreditWithOfferMapper;
import com.lulobank.credits.starter.v3.util.Messages;
import com.lulobank.credits.v3.port.in.promissorynote.dto.SignPromissoryNoteResponse;
import com.lulobank.credits.v3.usecase.AcceptOfferV3UseCase;
import com.lulobank.credits.v3.usecase.command.AcceptOffer;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Option;
import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v3/loan/client/{idClient}")
@CustomLog
public class CreditsInboundAdapterV3 {

    private final AcceptOfferV3UseCase acceptOfferV3UseCase;

    @Autowired
    public CreditsInboundAdapterV3(AcceptOfferV3UseCase acceptOfferV3UseCase) {
        this.acceptOfferV3UseCase = acceptOfferV3UseCase;
    }

    @PostMapping(value = "/offer/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Accept a loan offer", notes = "Update the given credit as an accepted offer. " +
            "Additionally create the promissory note for the new loan.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully accept offer", response = AcceptOfferResponse.class),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 406, message = "Error validating OTP"),
            @ApiResponse(code = 502, message = "idOffer not found | idCredit not found | Error getting info from " +
                    "savings")})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> acceptOffer(@RequestHeader final HttpHeaders headers,
                                              @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                              @Valid @RequestBody final CreditWithOfferV3Request creditWithOffer,
                                              BindingResult bindingResult) {

        return Option.of(bindingResult)
                .filter(Errors::hasErrors)
                .map(br -> new ResponseEntity<Object>(br.getAllErrors(), HttpStatus.BAD_REQUEST))
                .getOrElse(process(headers, idClient, creditWithOffer));
    }

    private ResponseEntity<Object> process(HttpHeaders headers, String idClient, CreditWithOfferV3Request creditWithOffer) {
            AcceptOffer acceptOffer = CreditWithOfferMapper.INSTANCE.toAcceptOffer(creditWithOffer, idClient);
            acceptOffer.setCredentials(new AdapterCredentials(headers.toSingleValueMap()));

            return acceptOfferV3UseCase.execute(acceptOffer)
                    .fold(this::mapError, this::mapResponse);
    }
    
    private ResponseEntity<Object> mapResponse(SignPromissoryNoteResponse signPromissoryNoteResponse) {
		return new ResponseEntity<>(new AcceptOfferResponse(signPromissoryNoteResponse.isValid()), HttpStatus.CREATED);
	}
    
    private ResponseEntity<Object> mapError(UseCaseResponseError useCaseResponseError) {

		return new ResponseEntity<>(new ErrorResponse(useCaseResponseError.getProviderCode(),
                useCaseResponseError.getBusinessCode(), useCaseResponseError.getDetail()), 
				getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
	}

}

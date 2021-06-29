package com.lulobank.credits.starter.inboundadapter;

import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.credits.sdk.dto.CreditErrorResult;
import com.lulobank.credits.sdk.dto.CreditSuccessResult;
import com.lulobank.credits.sdk.dto.clientproduct.offer.Offered;
import com.lulobank.credits.sdk.dto.initialofferv2.GetOfferToClient;
import com.lulobank.credits.services.features.riskmodelscore.model.ClientProductOffer;
import com.lulobank.credits.starter.v3.mappers.GetOffersByClientMapper;
import com.lulobank.credits.v3.usecase.intialsoffersv3.InitialsOffersV3UseCase;
import com.lulobank.credits.v3.usecase.intialsoffersv3.command.GetOffersByClient;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static com.lulobank.credits.services.utils.ResponseUtils.getResponseBindingResult;
import static com.lulobank.credits.services.utils.ResponseUtils.getResponseEntityError;


@RestController
@RequestMapping("/products/v2/")
@CrossOrigin(origins = "*")
@CustomLog
public class CreditsInboundAdapter {

    @Autowired
    private InitialsOffersV3UseCase initialsOffersV3UseCase;
    @Autowired
    @Qualifier("productOfferedClientDecoratorHandlerV2")
    private ValidatorDecoratorHandler productOfferedClientDecoratorHandlerV2;

    @PostMapping(value = "/loan/client/{idClient}/initial-offer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create initial offer", notes = "Create a loan as initial offer for the given client. The " +
            "new record includes loan conditions, risk information and flexible loans.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Create initial offer successfully", response = InitialOfferResponse.class),
            @ApiResponse(code = 400, message = "Validation error | Error in persist information in database")})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<InitialOfferResponse> initialOffer(@RequestHeader final HttpHeaders headers,
                                                             @Valid @PathVariable("idClient") @NotBlank(message = "IdClient is null or empty") String idClient,
                                                             @Valid @RequestBody final GetOfferToClient getOfferToClient,
                                                             BindingResult bindingResult) {
        getOfferToClient.setIdClient(idClient);
        return bindingResult.hasErrors() ?
                getResponseEntityError(getResponseBindingResult(bindingResult)) :
                getResponseEntityFromInitialOfferHandler(getOfferToClient);

    }

    @GetMapping(value = "offer/client/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get loan offer", notes = "Get the last loan offer for the given client.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Get loan offer successfully", response = Offered.class),
            @ApiResponse(code = 400, message = "Validation error")})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Offered> getProductOfferedClient(@RequestHeader final HttpHeaders headers, @PathVariable("idClient") final String idClient) {

        Response<Offered> response = productOfferedClientDecoratorHandlerV2.handle(new ClientProductOffer(idClient));

        if (Boolean.TRUE.equals(response.getHasErrors())) {
            return errorProcess(response);
        }
        return new ResponseEntity(new CreditSuccessResult<>(response.getContent()), HttpStatus.ACCEPTED);
    }

    private ResponseEntity<InitialOfferResponse> getResponseEntityFromInitialOfferHandler(GetOfferToClient getOfferToClient) {
        GetOffersByClient getOffersByClient= GetOffersByClientMapper.INSTANCE.getOffersByClientTO(getOfferToClient);
        return
                initialsOffersV3UseCase.execute(getOffersByClient)
                        .map(creditsV3Entity -> ResponseEntity.status(HttpStatus.CREATED).body(new InitialOfferResponse(creditsV3Entity.getIdCredit().toString())))
                        .recoverWith(error -> Try.of(() -> ResponseEntity.badRequest().build()))
                        .get();
    }

    private ResponseEntity errorProcess(Response<?> response) {

        return Option.ofOptional(response.getErrors().stream().findFirst())
                .map(ValidationResult::getValue)
                .map(s -> new ResponseEntity<>(new CreditErrorResult(response.getErrors()),
                        ValidatorUtils.getHttpStatusByCode(s)))
                .getOrElse(new ResponseEntity<>(new CreditErrorResult(response.getErrors()),
                        HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static
    class InitialOfferResponse {
        private String idCredit;
    }
}

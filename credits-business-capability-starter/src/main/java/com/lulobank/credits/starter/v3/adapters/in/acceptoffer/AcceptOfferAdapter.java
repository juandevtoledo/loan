package com.lulobank.credits.starter.v3.adapters.in.acceptoffer;

import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.handler.AcceptOfferHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/")
public class AcceptOfferAdapter {
	
	public final AcceptOfferHandler acceptOfferHandler;

	public AcceptOfferAdapter(AcceptOfferHandler acceptOfferHandler) {
		this.acceptOfferHandler = acceptOfferHandler;
	}

	@PostMapping(value = "loan/client/{idClient}/offer/accept", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Accept a loan offer", notes = "Close the given offer and create a new loan based on this " +
			"offer. Additionally create the promissory note for the new loan.")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successfully accept offer", response = AcceptOfferResponse.class),
			@ApiResponse(code = 406, message = "Error validating OTP | Error getting client info | Error getting " +
					"savings info | Error creating loan"),
			@ApiResponse(code = 502, message = "Error getting offer | Error getting credit")})
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<AdapterResponse> acceptOffer(@RequestHeader final HttpHeaders headers,
                                                            @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                                            @Valid @RequestBody final AcceptOfferRequest acceptOfferRequest) {
		return acceptOfferHandler.acceptOffer(idClient, acceptOfferRequest, headers);
	}
}

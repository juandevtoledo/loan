package com.lulobank.credits.starter.v3.adapters.in;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AddToWaitingRequest;
import com.lulobank.credits.starter.v3.handler.WaitingListHandler;
import com.lulobank.credits.starter.v3.util.Messages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v3/")
public class WaitingListAdapterV3 {

	private final WaitingListHandler waitingListHandler;

	public WaitingListAdapterV3(WaitingListHandler waitingListHandler) {
		this.waitingListHandler = waitingListHandler;
	}

	@PostMapping(value = "client/{idClient}/credit-waiting-list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Add client to credit waiting list", notes = "Add the given client to the waiting list for " +
			"the requested product offer")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Client added to waiting list successfully"),
			@ApiResponse(code = 406, message = "Error updating banner info")})
	public ResponseEntity<AdapterResponse> addToWaitingList(@RequestHeader final HttpHeaders headers,
                                                            @PathVariable("idClient") @NotBlank(message = Messages.ERROR_CLIENT_ID_MESSAGE) String idClient,
                                                            @Valid @RequestBody final AddToWaitingRequest addToWaitingRequest) {
		return waitingListHandler.addToWaitingList(idClient, addToWaitingRequest, headers);
	}
}

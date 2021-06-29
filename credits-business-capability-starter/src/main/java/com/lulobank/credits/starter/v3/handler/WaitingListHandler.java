package com.lulobank.credits.starter.v3.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.in.dto.AddToWaitingRequest;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.v3.usecase.waitinglist.AddToWaitingListUseCase;
import com.lulobank.credits.v3.usecase.waitinglist.command.AddToWaitingListRequest;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class WaitingListHandler {

	private final AddToWaitingListUseCase addToWaitingListUseCase;

	public WaitingListHandler(AddToWaitingListUseCase addToWaitingListUseCase) {
		this.addToWaitingListUseCase = addToWaitingListUseCase;
	}

	public ResponseEntity<AdapterResponse> addToWaitingList(String idClient, AddToWaitingRequest addToWaitingRequest,
                                                            HttpHeaders headers) {

		return addToWaitingListUseCase.execute(buildAddToWaitingListRequest(idClient, addToWaitingRequest, headers))
				.fold(this::mapError, ok -> new ResponseEntity<>(HttpStatus.OK));
	}

	private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError useCaseResponseError) {

		return new ResponseEntity<>(new ErrorResponse(useCaseResponseError.getProviderCode(),
                useCaseResponseError.getBusinessCode(), useCaseResponseError.getDetail()), HttpStatus.NOT_ACCEPTABLE);
	}

	private AddToWaitingListRequest buildAddToWaitingListRequest(String idClient,
			AddToWaitingRequest addToWaitingRequest, HttpHeaders headers) {
		
		return AddToWaitingListRequest.builder()
				.idClient(idClient)
				.auth(headers.toSingleValueMap())
				.idProductOffer(addToWaitingRequest.getIdProductOffer())
				.build();
	}
}

package com.lulobank.credits.starter.v3.handler;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferRequest;
import com.lulobank.credits.starter.v3.adapters.in.acceptoffer.dto.AcceptOfferResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.credits.v3.usecase.acceptoffer.AcceptOfferUseCase;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferCommand;
import com.lulobank.credits.v3.usecase.acceptoffer.command.AcceptOfferUseCaseResponse;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class AcceptOfferHandler {
	
	private final AcceptOfferUseCase acceptOfferUseCase;
	
	public AcceptOfferHandler(AcceptOfferUseCase acceptOfferUseCase) {
		this.acceptOfferUseCase = acceptOfferUseCase;
	}

	public ResponseEntity<AdapterResponse> acceptOffer(String idClient, AcceptOfferRequest acceptOfferRequest,
			HttpHeaders headers) {
		return acceptOfferUseCase.execute(buildAcceptOfferCommand(idClient, acceptOfferRequest, headers))
				.fold(this::mapError, this::mapResponse);
	}
	
	private ResponseEntity<AdapterResponse> mapResponse(AcceptOfferUseCaseResponse acceptOfferUseCaseResponse) {
		return new ResponseEntity<>(new AcceptOfferResponse(acceptOfferUseCaseResponse.isValid()), HttpStatus.CREATED);
	}
	
	private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError useCaseResponseError) {

		return new ResponseEntity<>(new ErrorResponse(useCaseResponseError.getProviderCode(),
                useCaseResponseError.getBusinessCode(), useCaseResponseError.getDetail()), 
				getHttpStatusFromBusinessCode(useCaseResponseError.getBusinessCode()));
	}

	private AcceptOfferCommand buildAcceptOfferCommand(String idClient, AcceptOfferRequest acceptOfferRequest,
			HttpHeaders headers) {
		return AcceptOfferCommand.builder()
				.idClient(idClient)
				.idCredit(acceptOfferRequest.getIdCredit())
				.idOffer(acceptOfferRequest.getIdOffer())
				.idProductOffer(acceptOfferRequest.getIdProductOffer())
				.automaticDebitPayments(acceptOfferRequest.isAutomaticDebitPayments())
				.confirmationLoanOTP(acceptOfferRequest.getConfirmationLoanOTP())
				.credentials(new AdapterCredentials(headers.toSingleValueMap()))
				.dayOfPay(acceptOfferRequest.getDayOfPay())
				.installment(acceptOfferRequest.getInstallment())
				.loanPurpose(acceptOfferRequest.getLoanPurpose())
				.build();
	}
}

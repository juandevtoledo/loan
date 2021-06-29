package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.PreapprovedLoanOffersRequest;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.PreapprovedLoanOffersMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.PreapprovedLoanOffersUseCase;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.command.GetOffersByIdClient;
import com.lulobank.credits.v3.usecase.preappoveloanoffers.dto.OfferedResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@RequiredArgsConstructor
public class PreapprovedLoanOfferHandler {

    private final PreapprovedLoanOffersUseCase preapprovedLoanOffersUseCase;

    public ResponseEntity<AdapterResponse> generateProductOffer(PreapprovedLoanOffersRequest request, String idClient) {

        return preapprovedLoanOffersUseCase.execute(buildRequest(request.getClientLoanRequestedAmount(), idClient))
                .fold(this::mapError, this::mapResponse);
    }

    private GetOffersByIdClient buildRequest(BigDecimal clientLoanRequestedAmount, String idClient) {
        return new GetOffersByIdClient(idClient, clientLoanRequestedAmount);
    }

    private ResponseEntity<AdapterResponse> mapResponse(OfferedResponse offeredResponse) {
        return AdapterResponseUtil.ok(PreapprovedLoanOffersMapper.INSTANCE.preapprovedLoanOffersResponseTo(offeredResponse));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

}

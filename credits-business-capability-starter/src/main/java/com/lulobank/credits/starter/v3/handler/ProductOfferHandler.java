package com.lulobank.credits.starter.v3.handler;

import com.lulobank.credits.starter.v3.adapters.in.dto.AdapterResponse;
import com.lulobank.credits.starter.v3.adapters.in.dto.ProductOfferRequest;
import com.lulobank.credits.starter.v3.mappers.GenericResponseMapper;
import com.lulobank.credits.starter.v3.mappers.ProductOfferRequestMapper;
import com.lulobank.credits.starter.v3.mappers.ProductOfferResponseMapper;
import com.lulobank.credits.starter.v3.util.AdapterResponseUtil;
import com.lulobank.credits.v3.port.in.productoffer.GenerateProductOfferPort;
import com.lulobank.credits.v3.port.in.productoffer.dto.ProductOffer;
import com.lulobank.credits.v3.usecase.productoffer.command.GenerateOfferRequest;
import com.lulobank.credits.v3.vo.AdapterCredentials;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.error;
import static com.lulobank.credits.starter.v3.util.AdapterResponseUtil.getHttpStatusFromBusinessCode;

@CustomLog
@AllArgsConstructor
public class ProductOfferHandler {

    private final GenerateProductOfferPort generateProductOfferPort;

    public ResponseEntity<AdapterResponse> generateProductOffer(ProductOfferRequest request, String idClient, HttpHeaders headers) {
        return generateProductOfferPort.execute(buildRequest(request, idClient, headers))
                .fold(this::mapError, this::mapResponse);
    }

    private GenerateOfferRequest buildRequest(ProductOfferRequest request, String idClient, HttpHeaders headers) {
        request.setAdapterCredentials(new AdapterCredentials(headers.toSingleValueMap()));
        request.setIdClient(idClient);
        return ProductOfferRequestMapper.INSTANCE.toGenerateOfferRequest(request);
    }

    private ResponseEntity<AdapterResponse> mapResponse(ProductOffer response) {
        return AdapterResponseUtil.ok(ProductOfferResponseMapper.INSTANCE.toProductOfferResponse(response));
    }

    private ResponseEntity<AdapterResponse> mapError(UseCaseResponseError error) {
        return error(GenericResponseMapper.INSTANCE.toErrorResponse(error),
                getHttpStatusFromBusinessCode(error.getBusinessCode()));
    }

}

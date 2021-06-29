package com.lulobank.credits.starter.v3.adapters.out.pep;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.out.pep.dto.GetPepServiceResponse;
import com.lulobank.credits.v3.port.out.pep.PepError;
import com.lulobank.credits.v3.port.out.pep.PepService;
import com.lulobank.credits.v3.port.out.pep.dto.GetPepResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import lombok.CustomLog;

@CustomLog
public class PepServiceAdapter implements PepService {

	private static final String CLIENTS_PEP_RESOURCE = "clients/api/v3/client/%s/pep";

	private final RestTemplateClient clientsRestTemplateClient;

	public PepServiceAdapter(RestTemplateClient clientsRestTemplateClient) {
		this.clientsRestTemplateClient = clientsRestTemplateClient;
	}

	@Override
	public Either<UseCaseResponseError, GetPepResponse> getPep(String idClient, Map<String,String> auth) {
		
		log.info("[PepServiceAdapter] getPep()");
		String context = String.format(CLIENTS_PEP_RESOURCE, idClient);
		
		return clientsRestTemplateClient.get(context, auth, GetPepServiceResponse.class)
			.peekLeft(error -> log.error(String.format("Error getting data from PepService(): %s", error.getBody())))
			.mapLeft(error -> PepError.errorGettingData())
			.map(ResponseEntity::getBody)
			.map(this::mapGetResponse)
			.mapLeft(UseCaseResponseError::map);
	}
	
	private GetPepResponse mapGetResponse(GetPepServiceResponse getPepServiceResponse) {
		GetPepResponse getPepResponse = new GetPepResponse();
		getPepResponse.setPep(getPepServiceResponse.getPep());
		return getPepResponse;
	}
}

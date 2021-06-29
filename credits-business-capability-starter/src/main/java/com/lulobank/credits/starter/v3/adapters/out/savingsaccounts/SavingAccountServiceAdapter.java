package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.credits.starter.v3.adapters.out.savingsaccounts.dto.SavingAccountType;
import com.lulobank.credits.v3.port.out.saving.SavingAccountError;
import com.lulobank.credits.v3.port.out.saving.SavingAccountService;
import com.lulobank.credits.v3.port.out.saving.dto.GetSavingAcountTypeResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;
import io.vavr.control.Either;
import lombok.CustomLog;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@CustomLog
public class SavingAccountServiceAdapter implements SavingAccountService {

	private static final String GET_SAVING_RESOURCE = "savingsaccounts/account/client/%s";

	private final RestTemplateClient savingsRestTemplateClient;

	public SavingAccountServiceAdapter(RestTemplateClient savingsRestTemplateClient) {
		this.savingsRestTemplateClient = savingsRestTemplateClient;
	}

	@Override
	public Either<UseCaseResponseError, GetSavingAcountTypeResponse> getSavingAccount(String idClient, Map<String,String> auth) {
		log.info("[SavingAccountServiceAdapter] getSavingAccount()");
		String context = String.format(GET_SAVING_RESOURCE, idClient);

		return savingsRestTemplateClient.get(context, auth, SavingAccountType.class)
				.peekLeft(error -> log.error("Error getting data from getSavingAccount(): %s", error.getBody()))
				.mapLeft(error -> SavingAccountError.errorGettingData())
				.map(ResponseEntity::getBody)
				.map(this::mapGetResponse)
				.mapLeft(UseCaseResponseError::map);

	}
	
	private GetSavingAcountTypeResponse mapGetResponse(SavingAccountType savingAccountType) {
		
		return GetSavingAcountTypeResponse.builder()
			.idSavingAccount(savingAccountType.getContent().getIdSavingAccount())
			.state(savingAccountType.getContent().getState())
			.type(savingAccountType.getContent().getType())
			.build();
	}
}

package com.lulobank.credits.starter.v3.adapters.out.savingsaccounts;

import co.com.lulobank.tracing.restTemplate.HttpError;
import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.credits.starter.v3.mappers.SavingsAccountsMapper;
import com.lulobank.credits.v3.port.in.savingsaccount.SavingsAccountV3Service;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountRequest;
import com.lulobank.credits.v3.port.in.savingsaccount.dto.SavingsAccountResponse;
import com.lulobank.savingsaccounts.sdk.dto.createsavingaccountv3.SavingAccountCreated;
import io.vavr.control.Either;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class SavingsAccountsV3ServiceAdapter implements SavingsAccountV3Service {

    private static final String SAVING_CREATE_RESOURCE = "savingsaccounts/v3/client/%s/create";
    
	private final RestTemplateClient savingsRestTemplateClient;

    public SavingsAccountsV3ServiceAdapter(RestTemplateClient savingsRestTemplateClient) {
        this.savingsRestTemplateClient = savingsRestTemplateClient;
    }

    @Override
    public Either<HttpError, SavingsAccountResponse> create(SavingsAccountRequest savingsAccountRequest, Map<String,String> auth) {
    	
    	String context = String.format(SAVING_CREATE_RESOURCE, savingsAccountRequest.getIdClient());
    	
    	return savingsRestTemplateClient.post(context, SavingsAccountsMapper.INSTANCE.toSavingsAccountRequest(savingsAccountRequest), auth, SavingAccountCreated.class)
    			.map(ResponseEntity::getBody)
    			.map(SavingsAccountsMapper.INSTANCE::toSavingsAccountResponse);
    } 
}



package com.lulobank.credits.v3.port.out.clients;

import java.util.Map;

import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import io.vavr.control.Either;

public interface ClientService {
	
	Either<UseCaseResponseError, ClientInformationResponse> getClientInformation(String idClient, Map<String,String> auth);
}

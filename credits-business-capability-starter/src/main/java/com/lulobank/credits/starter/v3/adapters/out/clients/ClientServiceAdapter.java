package com.lulobank.credits.starter.v3.adapters.out.clients;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.lulobank.credits.starter.v3.adapters.out.clients.dto.GetClientInformationResponse;
import com.lulobank.credits.starter.v3.adapters.out.clients.dto.GetClientInformationResponse.AdditionalPersonalInfo;
import com.lulobank.credits.starter.v3.adapters.out.clients.dto.GetClientInformationResponse.Content;
import com.lulobank.credits.v3.port.out.clients.ClientService;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse.ClientAdditionalPersonalInfo;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse.Phone;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.CustomLog;

@CustomLog
public class ClientServiceAdapter implements ClientService {

	private static final String GET_CLIENT_BY_ID_RESOURCE = "clients/idClient/%s";
	private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final RestTemplateClient clientsRestTemplateClient;

	public ClientServiceAdapter(RestTemplateClient clientsRestTemplateClient) {
		this.clientsRestTemplateClient = clientsRestTemplateClient;
	}

	@Override
	public Either<UseCaseResponseError, ClientInformationResponse> getClientInformation(String idClient,
			Map<String, String> auth) {

		log.info("[ClientServiceAdapter] getClientInformation()");
		String context = String.format(GET_CLIENT_BY_ID_RESOURCE, idClient);
		
		return clientsRestTemplateClient.get(context, auth, GetClientInformationResponse.class)
				.peekLeft(error -> log.error("Error getting data from getClientInformation(): %s", error.getBody()))
				.mapLeft(error -> new UseCaseResponseError("CRE_100", "500", "S_CL"))
				.map(ResponseEntity::getBody).map(this::mapGetResponse);
	}

	private ClientInformationResponse mapGetResponse(GetClientInformationResponse getClientInformationResponse) {
		
		return ClientInformationResponse.builder()
			.idClient(getClientInformationResponse.getContent().getIdClient())
			.documentNumber(getClientInformationResponse.getContent().getIdCard())
			.expeditionDate(getClientInformationResponse.getContent().getExpeditionDate())
			.documentType(getClientInformationResponse.getContent().getTypeDocument())
			.birthDate(getBirthdate(getClientInformationResponse.getContent()))
			.name(getClientInformationResponse.getContent().getName())
			.lastName(getClientInformationResponse.getContent().getLastName())
			.gender(getClientInformationResponse.getContent().getGender())
			.email(getClientInformationResponse.getContent().getEmailAddress())
			.phone(Phone.builder()
					.number(getClientInformationResponse.getContent().getPhoneNumber())
					.prefix(getClientInformationResponse.getContent().getPhonePrefix())
					.build())
			.clientAdditionalPersonalInfo(
					mapAdditionalPersonalInfo(getClientInformationResponse.getContent().getAdditionalPersonalInfo()))
			.build();
	}
	
	private ClientAdditionalPersonalInfo mapAdditionalPersonalInfo(AdditionalPersonalInfo additionalPersonalInfo) {
		return Option.of(additionalPersonalInfo)
				.map(info -> ClientAdditionalPersonalInfo.builder()
						.firstName(info.getFirstName())
						.secondName(info.getSecondName())
						.firstSurname(info.getFirstSurname())
						.secondSurname(info.getSecondSurname())
						.build())
				.getOrElse(ClientAdditionalPersonalInfo.builder().build());
	}

	private String getBirthdate(Content content) {
		return Option.of(content.getBirthDate())
			.map(birthdate ->  birthdate.format(BIRTH_DATE_FORMATTER))
			.getOrNull();
	}
}

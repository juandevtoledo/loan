package com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine;

import com.lulobank.credits.starter.v3.adapters.out.sqs.riskengine.RequestRiskEngineEvent.Phone;
import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;
import com.lulobank.credits.v3.port.out.queue.RiskEngineNotificationService;
import com.lulobank.events.api.EventFactory;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class RiskEngineNotificationServiceSqsAdapter implements RiskEngineNotificationService {

	private final SqsBraveTemplate sqsBraveTemplate;
	private final String clientsQueue;

	public RiskEngineNotificationServiceSqsAdapter(SqsBraveTemplate sqsBraveTemplate, String clientsQueue) {
		this.sqsBraveTemplate = sqsBraveTemplate;
		this.clientsQueue = clientsQueue;

	}

	@Override
	public Try<Void> sendRiskEngineNotification(ClientInformationResponse clientInformationResponse) {
		log.info("[RiskEngineNotificationServiceSqsAdapter] sendRiskEngineNotification()");
		return Try.run(() -> sqsBraveTemplate.convertAndSend(clientsQueue,
				EventFactory.ofDefaults(buildRequestRiskEngineEvent(clientInformationResponse)).build()));
	}

	private RequestRiskEngineEvent buildRequestRiskEngineEvent(
			ClientInformationResponse clientInformationResponse) {

		
		return RequestRiskEngineEvent.builder()
			.idClient(clientInformationResponse.getIdClient())
			.documentNumber(clientInformationResponse.getDocumentNumber())
			.expeditionDate(clientInformationResponse.getExpeditionDate())
			.documentType(clientInformationResponse.getDocumentType())
			.birthDate(clientInformationResponse.getBirthDate())
			.name(clientInformationResponse.getName())
			.lastName(clientInformationResponse.getLastName())
			.gender(clientInformationResponse.getGender())
			.email(clientInformationResponse.getEmail())
			.phone(buildPhone(clientInformationResponse.getPhone()))
			.build();
	}

	private Phone buildPhone(com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse.Phone clientInformationPhone) {
		return Option.of(clientInformationPhone)
			.map(phone -> Phone.builder()
					.number(phone.getNumber())
					.prefix(phone.getPrefix())
					.build())
			.getOrNull();
	}
}

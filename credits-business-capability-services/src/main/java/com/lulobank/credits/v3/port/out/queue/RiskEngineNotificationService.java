package com.lulobank.credits.v3.port.out.queue;

import com.lulobank.credits.v3.port.out.clients.dto.ClientInformationResponse;

import io.vavr.control.Try;

public interface RiskEngineNotificationService {

	Try<Void> sendRiskEngineNotification(ClientInformationResponse clientInformationResponse);
}

package com.lulobank.credits.starter.v3.adapters.in.sqs.handler;

import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseMessage;
import com.lulobank.credits.v3.port.in.approvedriskengine.RiskEngineResponseUseCase;
import com.lulobank.events.api.EventHandler;
import io.vavr.control.Try;

public class RiskEngineResponseEventHandler implements EventHandler<RiskEngineResponseMessage> {

	private final RiskEngineResponseUseCase riskEngineResponseUseCase;

	public RiskEngineResponseEventHandler(RiskEngineResponseUseCase riskEngineResponseUseCase) {
		this.riskEngineResponseUseCase = riskEngineResponseUseCase;
	}

	@Override
	public Try<Void> execute(RiskEngineResponseMessage payload) {
		return riskEngineResponseUseCase.execute(payload);
	}

	@Override
	public Class<RiskEngineResponseMessage> eventClass() {
		return RiskEngineResponseMessage.class;
	}
}

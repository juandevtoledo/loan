package com.lulobank.credits.v3.port.in.approvedriskengine;

import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import com.lulobank.credits.v3.util.UseCase;
import io.vavr.control.Try;
import lombok.CustomLog;

@CustomLog
public class RiskEngineResponseUseCase implements UseCase<RiskEngineResponseMessage, Try<Void>> {

	private final CreditsV3Repository creditsV3Repository;

	public RiskEngineResponseUseCase(CreditsV3Repository creditsV3Repository) {
		this.creditsV3Repository = creditsV3Repository;
	}

	@Override
	public Try<Void> execute(RiskEngineResponseMessage riskEngineResponseMessage) {
		
		return creditsV3Repository
				.save(RiskEngineResponseMapper.INSTANCE
				.riskEngineResponseMessageToCreditsV3Entity(riskEngineResponseMessage))
				.flatMap(credit -> Try.run(() -> log.info(String.format("PREAPPROVED was saved. idClient: %s",
						riskEngineResponseMessage.getIdClient()))))
				.onFailure(ex -> log.error(
						String.format("Error saving PREAPPROVED. idClient: %s", riskEngineResponseMessage.getIdClient()), ex));
	}
}

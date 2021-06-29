package com.lulobank.credits.starter.v3.checker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lulobank.healthcheck.core.HealthChecker.ConfigError;
import com.lulobank.healthcheck.core.HealthChecker.ConfigPresent;
import com.lulobank.healthcheck.spring.LuloHealthIndicator;

import flexibility.client.models.response.ConfigValueResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.Function0;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.CustomLog;

@CustomLog
@AllArgsConstructor
public class FlexibilitySdkConfigChecker extends LuloHealthIndicator {
	
	private final FlexibilitySdk flexibilitySdk;

	@Override
	protected Function0<Either<ConfigError, ConfigPresent>> checker() {
		return Function0.of(() -> 
			Try.of(() -> flexibilitySdk.getConfig("loan.insurrance.rate"))
				.flatMap(this::configValueResponseToMap)
				.map(map -> ConfigPresent.of(map))
				.onFailure(error -> log.error(String.format("Error trying to get Flexibility configuration: %s", error)))
				.toEither(this::mapError)).memoized();
	}
	
	private ConfigError mapError() {
		return ConfigError.of("Impossible to get configuration from flexibility");
	}
	
	private Try<Map<String, String>> configValueResponseToMap(List<ConfigValueResponse> configList) {
		return Try.of(() -> configList
		.stream()
		.collect(Collectors.toMap(ConfigValueResponse::getName , ConfigValueResponse::getValue)));
	}
}

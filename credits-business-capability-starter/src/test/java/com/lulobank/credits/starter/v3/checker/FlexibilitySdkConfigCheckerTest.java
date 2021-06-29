package com.lulobank.credits.starter.v3.checker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.healthcheck.core.HealthChecker.ConfigError;
import com.lulobank.healthcheck.core.HealthChecker.ConfigPresent;

import flexibility.client.connector.ProviderException;
import flexibility.client.models.response.ConfigValueResponse;
import flexibility.client.sdk.FlexibilitySdk;
import io.vavr.control.Either;

public class FlexibilitySdkConfigCheckerTest {

	private FlexibilitySdkConfigChecker flexibilitySdkConfigChecker;

	@Mock
	private FlexibilitySdk flexibilitySdk;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		flexibilitySdkConfigChecker = new FlexibilitySdkConfigChecker(flexibilitySdk);
	}

	@Test
	public void flexibilityCheckerShouldReturnRight() throws ProviderException {
		when(flexibilitySdk.getConfig("loan.insurrance.rate")).thenReturn(buildConfigValueResponse());
		Either<ConfigError, ConfigPresent> response = flexibilitySdkConfigChecker.checker().get();
		assertThat(response.isRight(), is(true));
	}

	@Test
	public void flexibilityCheckerShouldReturnLeft() throws ProviderException {
		when(flexibilitySdk.getConfig("loan.insurrance.rate")).thenThrow(new ProviderException("message", "errorCode"));
		Either<ConfigError, ConfigPresent> response = flexibilitySdkConfigChecker.checker().get();
		assertThat(response.isLeft(), is(true));
	}

	private List<ConfigValueResponse> buildConfigValueResponse() {
		ConfigValueResponse configValueResponse = new ConfigValueResponse();
		configValueResponse.setName("name");
		configValueResponse.setValue("value");
		List<ConfigValueResponse> list = new ArrayList<>();
		list.add(configValueResponse);
		return list;
	}
}

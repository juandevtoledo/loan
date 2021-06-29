package com.lulobank.credits.v3.port.out.saving;

import com.lulobank.credits.v3.util.HttpDomainStatus;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class SavingAccountError extends UseCaseResponseError {

	private SavingAccountError(SavingAccountErrorStatus pepErrorStatus, String providerCode) {
		super(pepErrorStatus.name(), providerCode, SavingAccountErrorStatus.DEFAULT_DETAIL);
	}

	public static SavingAccountError errorGettingData() {
		return new SavingAccountError(SavingAccountErrorStatus.CRE_112, HttpDomainStatus.BAD_GATEWAY.toString());
	}

	public static SavingAccountError stateValidationFailed() {
		return new SavingAccountError(SavingAccountErrorStatus.CRE_114, HttpDomainStatus.NOT_ACCEPTABLE.toString());
	}
}

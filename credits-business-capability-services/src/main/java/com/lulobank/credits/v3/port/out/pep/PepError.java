package com.lulobank.credits.v3.port.out.pep;

import static com.lulobank.credits.v3.port.out.pep.PepErrorStatus.CRE_111;
import static com.lulobank.credits.v3.port.out.pep.PepErrorStatus.CRE_113;
import static com.lulobank.credits.v3.port.out.pep.PepErrorStatus.DEFAULT_DETAIL;
import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_GATEWAY;
import static com.lulobank.credits.v3.util.HttpDomainStatus.NOT_ACCEPTABLE;

import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class PepError extends UseCaseResponseError {

	private PepError(PepErrorStatus pepErrorStatus, String providerCode) {
		super(pepErrorStatus.name(), providerCode, DEFAULT_DETAIL);
	}

	public static PepError errorGettingData() {
		return new PepError(CRE_111, BAD_GATEWAY.toString());
	}

	public static PepError pepValidationFailed() {
		return new PepError(CRE_113, NOT_ACCEPTABLE.toString());
	}
}

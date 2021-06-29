package com.lulobank.credits.v3.vo;

import com.lulobank.credits.v3.port.out.corebanking.CoreBankingError;
import com.lulobank.credits.v3.util.HttpDomainStatus;

import static com.lulobank.credits.v3.util.HttpDomainStatus.BAD_GATEWAY;
import static com.lulobank.credits.v3.util.HttpDomainStatus.INTERNAL_SERVER_ERROR;
import static com.lulobank.credits.v3.util.HttpDomainStatus.NOT_ACCEPTABLE;
import static com.lulobank.credits.v3.util.HttpDomainStatus.NOT_FOUND;

public class CreditsError extends UseCaseResponseError {

    private CreditsError(String creditsErrorStatus, String providerCode, String detail) {
        super(creditsErrorStatus, providerCode, detail);
    }

    private CreditsError(CreditsErrorStatus creditsErrorStatus, HttpDomainStatus httpDomainStatus, String detail) {
        super(creditsErrorStatus.name(), httpDomainStatus, detail);
    }

    public static CreditsError databaseError() {
        return new CreditsError(CreditsErrorStatus.CRE_101, NOT_FOUND, CreditsErrorStatus.DATA_DETAIL);
    }

    public static CreditsError generateOfferError() {
        return new CreditsError(CreditsErrorStatus.CRE_102, NOT_ACCEPTABLE, CreditsErrorStatus.UNKNOWN_DETAIL);
    }

    public static CreditsError unknownError() {
        return new CreditsError(CreditsErrorStatus.CRE_100, INTERNAL_SERVER_ERROR, CreditsErrorStatus.UNKNOWN_DETAIL);
    }

    public static CreditsError persistError() {
        return new CreditsError(CreditsErrorStatus.CRE_110, BAD_GATEWAY, CreditsErrorStatus.DATA_DETAIL);
    }
    
	public static CreditsError validateOtpError() {
		return new CreditsError(CreditsErrorStatus.CRE_116, NOT_ACCEPTABLE, CreditsErrorStatus.OTP_VALIDATION_DETAIL);
	}
	
	public static CreditsError idCreditNotFound() {
		return new CreditsError(CreditsErrorStatus.CRE_117, BAD_GATEWAY, CreditsErrorStatus.DATA_DETAIL);
	}

	public static CreditsError idOfferNotFound() {
		return new CreditsError(CreditsErrorStatus.CRE_118, BAD_GATEWAY, CreditsErrorStatus.DATA_DETAIL);
	}

	public static CreditsError errorCreatingLoanProvider() {
		return new CreditsError(CreditsErrorStatus.CRE_119, NOT_ACCEPTABLE, CreditsErrorStatus.CORE_DETAIL);
	}
	
	public static CreditsError errorWithClientInfo() {
		return new CreditsError(CreditsErrorStatus.CRE_120, NOT_ACCEPTABLE, CreditsErrorStatus.CORE_DETAIL);
	}

    public static CreditsError toCreditError(CoreBankingError error){
        return  new CreditsError(error.getBusinessCode(),error.getProviderCode(),error.getDetail());
    }

}

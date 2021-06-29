package com.lulobank.credits.v3.port.out.corebanking;

import com.lulobank.credits.v3.util.HttpDomainStatus;
import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class CoreBankingError extends UseCaseResponseError {

    private CoreBankingError(CoreBankingErrorStatus creditsErrorStatus, String providerCode) {
        super(creditsErrorStatus.name(), providerCode, CoreBankingErrorStatus.DEFAULT_DETAIL);
    }

    public static CoreBankingError getParametersError() {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_103, HttpDomainStatus.BAD_GATEWAY.toString());
    }

    public static CoreBankingError accountBlocked() {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_109,HttpDomainStatus.BAD_GATEWAY.toString());
    }

    public static CoreBankingError paymentError(String businessCode) {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_106,businessCode);
    }

    public static CoreBankingError clientWithOutAccountsError(String businessCode) {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_107, businessCode);
    }

    public static CoreBankingError clientWithOutAccountsError() {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_107, HttpDomainStatus.BAD_GATEWAY.toString());
    }

    public static CoreBankingError defaultError() {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_108, HttpDomainStatus.BAD_GATEWAY.toString());
    }

    public static CoreBankingError buildGettingDataLoanError(String businessCode) {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_103,businessCode);
    }

    public static CoreBankingError simulateLoanError(String businessCode) {
        return new CoreBankingError(CoreBankingErrorStatus.CRE_110,businessCode);
    }

}

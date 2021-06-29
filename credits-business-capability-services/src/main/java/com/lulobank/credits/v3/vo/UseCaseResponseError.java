package com.lulobank.credits.v3.vo;

import com.lulobank.credits.v3.util.HttpDomainStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseResponseError {
    private final String businessCode;
    private final String providerCode;
    private final String detail;

    public UseCaseResponseError(String businessCode, String providerCode) {
        this.businessCode = businessCode;
        this.providerCode = providerCode;
        this.detail = CreditsErrorStatus.DATA_DETAIL;
    }

    public UseCaseResponseError(String businessCode, String providerCode,String detail) {
        this.businessCode = businessCode;
        this.providerCode = providerCode;
        this.detail = detail;
    }

    public UseCaseResponseError(String businessCode, HttpDomainStatus httpDomainStatus, String detail) {
        this.businessCode = businessCode;
        this.providerCode = String.valueOf(httpDomainStatus.value());
        this.detail = detail;
    }

    public static  <T extends UseCaseResponseError> UseCaseResponseError map(T t){
        return new UseCaseResponseError(t.getBusinessCode(),t.getProviderCode(),t.getDetail());
    }

}

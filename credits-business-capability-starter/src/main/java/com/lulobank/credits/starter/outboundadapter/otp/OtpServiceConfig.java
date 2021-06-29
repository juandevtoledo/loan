package com.lulobank.credits.starter.outboundadapter.otp;

import com.lulobank.credits.services.port.inbound.OtpService;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpCreditOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtpServiceConfig {

    @Value("${services.otp.url}")
    private String serviceDomain;

    @Bean
    public OtpService getOtpService (){
        return new OtpServiceAdapter(getRetrofitOtpCreditOperation());
    }

    private OtpCreditOperations getRetrofitOtpCreditOperation(){
        return new RetrofitOtpCreditOperation(serviceDomain);
    }
}

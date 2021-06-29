package com.lulobank.credits.starter.v3.adapters.out.loan;

import brave.SpanCustomizer;
import com.lulobank.credits.starter.v3.adapters.config.CreditsConditionV3Config;
import com.lulobank.credits.v3.dto.CreditsConditionV3;
import com.lulobank.credits.v3.port.in.loan.LoanV3Service;
import com.lulobank.tracing.FunctionBrave;
import flexibility.client.FlexibilitySdkFactory;
import flexibility.client.models.Credentials;
import flexibility.client.sdk.FlexibilitySdk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CreditsConditionV3Config.class,})
public class LoanConfig {

    @Value("${flexibility.sdk.url}")
    private String url;

    @Value("${flexibility.sdk.clientId}")
    private String clientId;

    @Value("${flexibility.sdk.secret}")
    private String secret;



    @Bean
    public LoanV3Service getLoanV3Service(CreditsConditionV3 creditsConditionV3, FunctionBrave functionBrave, SpanCustomizer spanCustomizer){
        return new LoanAdapter(flexibilitySdk(),creditsConditionV3, functionBrave,spanCustomizer);
    }


    private FlexibilitySdk flexibilitySdk() {
        Credentials credentials = new Credentials();
        credentials.setSecret(secret);
        credentials.setClientId(clientId);
        return new FlexibilitySdkFactory().getSdk(url, credentials);
    }
}

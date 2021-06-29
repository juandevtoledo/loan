package com.lulobank.credits.starter.v3.adapters.out.otp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lulobank.credits.starter.v3.adapters.out.otp.ValidateOtpServiceAdapter;
import com.lulobank.credits.v3.port.out.otp.ValidateOtpService;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpCreditOperation;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ValidateOtpServiceConfig {

    @Value("${services.otp.url}")
    private String serviceDomain;

    @Bean("retrofitOtpClient")
    public Retrofit retrofitSavings(@Qualifier("okHttpBuilderTracing") OkHttpClient.Builder builder) {
        return new Retrofit.Builder()
                .baseUrl(serviceDomain)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    @Bean
    public ValidateOtpService getValidateOtpService(@Qualifier("retrofitOtpClient") Retrofit retrofit) {
    	return new ValidateOtpServiceAdapter(new RetrofitOtpCreditOperation(retrofit));
    }
}

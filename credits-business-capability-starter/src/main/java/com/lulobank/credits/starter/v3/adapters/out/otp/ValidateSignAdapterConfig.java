package com.lulobank.credits.starter.v3.adapters.out.otp;

import com.lulobank.credits.v3.port.in.promissorynote.ValidForPromissoryNoteSing;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpCreditOperation;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ValidateSignAdapterConfig {

    @Value("${services.otp.url}")
    private String serviceDomain;

    @Bean("retrofitOtp")
    public Retrofit retrofitSavings(@Qualifier("okHttpBuilderTracing") OkHttpClient.Builder builder) {
        return new Retrofit.Builder()
                .baseUrl(serviceDomain)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Bean
    public ValidForPromissoryNoteSing validateSing(@Qualifier("retrofitOtp") Retrofit retrofit) {
        return new ValidateSignV3Adapter(new RetrofitOtpCreditOperation(retrofit));
    }


}

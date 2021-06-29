package com.lulobank.credits.starter.config;

import com.lulobank.savingsaccounts.sdk.operations.ISavingsAccount;
import com.lulobank.savingsaccounts.sdk.operations.impl.RetrofitSavingsAccount;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class SavingsServiceConfig {

    @Value("${services.savings.url}")
    private String serviceDomain;

    @Bean
    public ISavingsAccount savingsAccountService(@Qualifier("retrofitSavings") Retrofit retrofit) {
        return new RetrofitSavingsAccount(retrofit);
    }

    @Bean("retrofitSavings")
    public Retrofit retrofitSavings(@Qualifier("okHttpBuilderTracing") OkHttpClient.Builder builder) {
        return new Retrofit.Builder()
                .baseUrl(serviceDomain)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

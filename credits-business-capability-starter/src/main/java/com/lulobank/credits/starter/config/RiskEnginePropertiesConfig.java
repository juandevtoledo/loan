package com.lulobank.credits.starter.config;

import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import com.lulobank.credits.services.outboundadapters.riskengine.IRiskEngineOperation;
import com.lulobank.credits.services.outboundadapters.riskengine.impl.RiskEngineOperation;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RiskEnginePropertiesConfig {

    @Value("${services.riskengine.url}")
    private String riskEngineUrl;

    @Value("${services.riskengine.connectionTimeOut}")
    private Integer connectionTimeOut;

    @Value("${services.riskengine.writeTimeOut}")
    private Integer writeTimeOut;

    @Value("${services.riskengine.readTimeOut}")
    private Integer readTimeOut;

    @Bean
    public IRiskEngineOperation riskEngineOperation(@Qualifier("risk") Retrofit retrofit) {
        return new RiskEngineOperation(retrofit);
    }

    @Bean("risk")
    public Retrofit riskRetrofit(HttpTracing tracing) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS);

        Try.of(() -> TracingInterceptor.create(tracing))
                .peek(builder::addNetworkInterceptor);
        return new Retrofit.Builder()
                .baseUrl(riskEngineUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

package com.lulobank.credits.starter.v3.adapters.out.promissorynote;


import co.com.lulobank.tracing.core.RetrofitTracingConfig;
import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.credits.v3.port.in.promissorynote.PromissoryNoteV3Service;
import com.lulobank.promissorynote.sdk.operations.impl.RetrofitPromissoryNote;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@Import({RetrofitTracingConfig.class})
public class PromissoryNoteAdapterConfig {

    @Value("${services.promissorynote.url}")
    private String serviceDomain;
    @Value("${services.promissorynote.connectionTimeOut}")
    private Integer connectionTimeOut;
    @Value("${services.promissorynote.readTimeOut}")
    private Integer readTimeOut;
    @Value("${services.promissorynote.writeTimeOut}")
    private Integer writeTimeOut;
    @Value("${cloud.aws.sqs.queue.promissorynote-events}")
    private String promissorynoteSqsEndpoint;

    @Bean
    public PromissoryNoteV3Service getPromissoryNoteService(@Qualifier("promissory") Retrofit retrofit) {
        return new PromissoryNoteServiceAdapter(new RetrofitPromissoryNote(retrofit));
    }

    @Bean("promissory")
    public Retrofit promissoryRetrofit(@Qualifier("okHttpBuilderTracing") OkHttpClient.Builder builder) {

        OkHttpClient.Builder timeoutBuilder = builder.readTimeout(readTimeOut, TimeUnit.SECONDS)
                .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl(serviceDomain)
                .client(timeoutBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
	@Bean
	public PromissoryNoteSqsAdapter getPromissoryNoteSQSAdpater(SqsBraveTemplate sqsBraveTemplate) {
		return new PromissoryNoteSqsAdapter(sqsBraveTemplate, promissorynoteSqsEndpoint);
	}
}

package com.lulobank.credits.starter.config;

import com.lulobank.promissorynote.sdk.operations.IPromissoryNote;
import com.lulobank.promissorynote.sdk.operations.impl.RetrofitPromissoryNote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class PromissoryNoteServiceConfig {

    @Bean
    public IPromissoryNote promissoryNoteService(@Qualifier("promissory") Retrofit retrofit) {
        return new RetrofitPromissoryNote(retrofit);
    }
}

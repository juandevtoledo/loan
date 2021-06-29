package com.lulobank.credits.starter;

import brave.sampler.Sampler;
import co.com.lulobank.tracing.error.tracking.SentryConfigurationRunner;
import co.com.lulobank.tracing.sqs.EventProcessorConfig;

import com.lulobank.credits.starter.v3.adapters.config.WaitingListConfig;
import com.lulobank.credits.starter.v3.checker.config.CheckerConfig;
import com.lulobank.events.impl.SqsConfig;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Locale;
import java.util.TimeZone;

@EnableCaching
@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages = {
  "com.lulobank.credits.starter.config",
  "com.lulobank.credits.services",
  "com.lulobank.credits.starter.inboundadapter",
  "com.lulobank.credits.starter.outboundadapter",
  "com.lulobank.credits.starter.v3.adapters.in",
  "com.lulobank.credits.starter.v3.adapters.config",
  "co.com.lulobank",
  "com.lulobank.logger"
}, basePackageClasses = {SentryConfigurationRunner.class, EventProcessorConfig.class, SqsConfig.class, WaitingListConfig.class, CheckerConfig.class})

@EnableDynamoDBRepositories(basePackages = {"com.lulobank.credits.services.outboundadapters.repository", "com.lulobank.credits.starter.v3.adapters.out.dynamo"})
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
    TimeZone.setDefault(TimeZone.getTimeZone("America/Bogota"));
    Locale.setDefault(Locale.forLanguageTag("es_CO"));
  }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}

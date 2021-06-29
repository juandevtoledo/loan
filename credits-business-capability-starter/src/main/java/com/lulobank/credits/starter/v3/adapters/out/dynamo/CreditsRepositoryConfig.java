package com.lulobank.credits.starter.v3.adapters.out.dynamo;

import com.lulobank.tracing.DatabaseBrave;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreditsRepositoryConfig {


    @Bean
    public CreditsV3Repository configCreditsRepositoryV3(DynamoDBMapper dynamoDBMapper, DatabaseBrave databaseBrave) {
        return new CreditsAdapterV3Repository(dynamoDBMapper, databaseBrave);
    }

}
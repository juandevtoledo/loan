package com.lulobank.credits.starter.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.lulobank.credits.services.utils.DynamoDBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component
public class DynamoDBHealthCheck implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDBHealthCheck.class);

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Override
    public Health health() {

        int errorCode = checkDynamoDB(); // perform some specific health check
        if (errorCode != 0) {
            return Health.up()
                    .withDetail(DynamoDBProperties.CUSTOM_DATABASE, DynamoDBProperties.NAME_DATABASE)
                    .withDetail(DynamoDBProperties.STATUS_DATABASE, Status.DOWN.getCode())
                    .build();
        }

        errorCode = checkDynamoDBSchema();
        if (errorCode != 0) {
            return Health.up()
                    .withDetail(DynamoDBProperties.CUSTOM_DATABASE, DynamoDBProperties.NAME_DATABASE)
                    .withDetail(DynamoDBProperties.STATUS_DATABASE, Status.UP.getCode())
                    .withDetail(DynamoDBProperties.CUSTOM_DATABASE, DynamoDBProperties.NAME_DATABASE)
                    .withDetail(DynamoDBProperties.SCHEMA_DATABASE, Status.DOWN.getCode())
                    .build();
        }
        return Health.up()
                .withDetail(DynamoDBProperties.CUSTOM_DATABASE, DynamoDBProperties.NAME_DATABASE)
                .withDetail(DynamoDBProperties.STATUS_DATABASE, Status.UP.getCode())
                .withDetail(DynamoDBProperties.CUSTOM_DATABASE, DynamoDBProperties.NAME_DATABASE)
                .withDetail(DynamoDBProperties.SCHEMA_DATABASE, Status.UP.getCode())
                .build();
    }
    private int checkDynamoDB() {
        try {
            ListTablesRequest request = new ListTablesRequest();
            request.setSdkClientExecutionTimeout(5000);
            amazonDynamoDB.listTables(request);
            return 0; //To verify that table Clients exist
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return -1;
        }
    }

    private int checkDynamoDBSchema() {
        try {
            ListTablesRequest request = new ListTablesRequest();
            ListTablesResult response = amazonDynamoDB.listTables(request);
            boolean clientTableExists = response.getTableNames().stream().anyMatch(x-> x.equals(DynamoDBProperties.TABLE_NAME));
            if(clientTableExists){
                return 0; 
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }
}

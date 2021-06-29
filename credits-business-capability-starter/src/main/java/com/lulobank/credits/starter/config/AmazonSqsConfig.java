package com.lulobank.credits.starter.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.lulobank.credits.services.features.services.SQSMessageService;
import com.lulobank.credits.services.features.services.model.SQSEndpoint;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
@Deprecated
public class AmazonSqsConfig {

    @Value("${cloud.aws.region.static}")
    private String amazonSQSRegion;

    @Value("${cloud.aws.sqs.endpoint}")
    private String sqsEndPoint;

    @Value("${cloud.aws.sqs.queue.client-events}")
    private String sqsEndPointClient;

    @Value("${cloud.aws.sqs.queue.saving-account-events}")
    private String sqsEndPointSavingAccount;

    @Value("${cloud.aws.sqs.queue.transaction-events}")
    private String sqsEndPointTransaction;

    @Value("${cloud.aws.sqs.queue.reporting-events}")
    private String sqsEndPointReporting;

    @Value("${cloud.aws.sqs.max-number-of-messages}")
    private int sqsMaxMessagesNumber;

    @Bean
    @Deprecated
    public SQSMessageService sqsMessageService(){
        return new SQSMessageService(createEndpoint(),new QueueMessagingTemplate(amazonSQSAsync()));
    }

    public SQSEndpoint createEndpoint(){
        SQSEndpoint sqsEndpoint = new SQSEndpoint();
        sqsEndpoint.setSqsEndPointClient(sqsEndPointClient);
        sqsEndpoint.setSqsEndPointSavingAccount(sqsEndPointSavingAccount);
        sqsEndpoint.setSqsEndPointTransaction(sqsEndPointTransaction);
        sqsEndpoint.setSqsEndpointReporting(sqsEndPointReporting);
        return sqsEndpoint;
    }

    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsEndPoint,amazonSQSRegion))
                .build();
    }

}
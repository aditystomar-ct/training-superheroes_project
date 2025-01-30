package com.cleartax.training_superheroes.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class SqsClientConfig {

    private final SqsConfig sqsConfig;


//    @Bean
//    public SqsClient sqsClient() {
//        return SqsClient.builder()
//                .region(Region.of(sqsConfig.getRegion()))
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsSessionCredentials.create(
//                                sqsConfig.getAccessKey(),
//                                sqsConfig.getSecretKey(),
//                                sqsConfig.getSessionToken()
//                        )
//                ))
//                .build();
//    }

    @Bean
    public AmazonSQS amazonSQS() {
        BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                sqsConfig.getAccessKey(),
                sqsConfig.getSecretKey(),
                sqsConfig.getSessionToken()
        );

        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:4566", // Main LocalStack endpoint
                                sqsConfig.getRegion()
                        )
                )
                .build();
    }

}
package com.cleartax.training_superheroes.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SqsConfig {

    @Value("${sqs.queue.name}")
    private String queueName;

    @Value("${sqs.queue.url}")
    private String queueUrl;

    @Value("${sqs.queue.region}")
    private String region;

    @Value("${sqs.queue.access-key}")
    private String accessKey;

    @Value("${sqs.queue.secret-key}")
    private String secretKey;

    @Value("${sqs.queue.session-token}")
    private String sessionToken;



}
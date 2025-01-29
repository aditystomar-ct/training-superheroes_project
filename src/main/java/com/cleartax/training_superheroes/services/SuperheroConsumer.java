package com.cleartax.training_superheroes.services;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.cleartax.training_superheroes.config.SqsConfig;
import com.cleartax.training_superheroes.dto.Superhero;
import com.cleartax.training_superheroes.dto.SuperheroRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Getter
public class SuperheroConsumer {

    private final SqsConfig sqsConfig;
    private final AmazonSQS amazonSQS;
    private final SuperheroService superheroService;
    private final ObjectMapper objectMapper;  // Making ObjectMapper a class-level variable

    @Autowired
    public SuperheroConsumer(SqsConfig sqsConfig, AmazonSQS amazonSQS, SuperheroService superheroService, ObjectMapper objectMapper) {
        this.sqsConfig = sqsConfig;
        this.amazonSQS = amazonSQS;
        this.superheroService = superheroService;
        this.objectMapper = objectMapper;  // Dependency injection of ObjectMapper
    }

    // Scheduled task to run every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void scheduledConsumeSuperhero() {
        consumeSuperhero();
    }

    public void consumeSuperhero() {
        try {
            // Receive messages from the queue
            ReceiveMessageResult receiveResult = amazonSQS.receiveMessage(new ReceiveMessageRequest()
                    .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue")
                    .withMaxNumberOfMessages(10) // Fetch up to 10 messages
                    .withWaitTimeSeconds(10)); // Long polling for up to 10 seconds

            if (receiveResult.getMessages().isEmpty()) {
                System.out.println("No messages available in the queue.");
                return;
            }

            // Iterate over each message
            receiveResult.getMessages().forEach(message -> {
                try {
                    String messageBody = message.getBody();
                    System.out.println("Received message: " + messageBody);

                    // Parse the JSON message to extract fields
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> messageMap = objectMapper.readValue(messageBody, Map.class);

                    // Make sure you're extracting values correctly
                    String superHeroName = messageMap.get("superHeroName");
                    String universe = messageMap.get("universe");

                    System.out.println("Parsed message - SuperHeroName: " + superHeroName + ", Universe: " + universe);

                    // Check if the superhero exists in the database
                    Superhero existingSuperhero = superheroService.getByName(superHeroName);

                    if (existingSuperhero != null) {
                        // If superhero exists, update it
                        SuperheroRequestBody updatedDetails = SuperheroRequestBody.builder()
                                .name(superHeroName)
                                .universe(universe)
                                .build();

                        // Update superhero in the database
                        System.out.println("Updating superhero: " + superHeroName);
                        superheroService.updateSuperhero(superHeroName, universe, updatedDetails);
                    } else {
                        System.out.println("Superhero not found in the database: " + superHeroName);
                    }

                    // Delete the processed message from the queue
                    amazonSQS.deleteMessage(new DeleteMessageRequest()
                            .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue")
                            .withReceiptHandle(message.getReceiptHandle()));

                    System.out.println("Deleted message from the queue: " + messageBody);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + message.getBody());
                    e.printStackTrace(); // Log full exception stack trace for debugging
                }
            });
        } catch (Exception e) {
            System.err.println("Error consuming messages from the queue.");
            e.printStackTrace(); // Log full exception stack trace for debugging
        }
    }
}

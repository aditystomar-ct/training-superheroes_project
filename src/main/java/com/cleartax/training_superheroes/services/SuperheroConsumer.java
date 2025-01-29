package com.cleartax.training_superheroes.services;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.cleartax.training_superheroes.config.SqsConfig;
import com.cleartax.training_superheroes.dto.Superhero;
import com.cleartax.training_superheroes.dto.SuperheroRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SuperheroConsumer {

    @Autowired
    private SqsConfig sqsConfig;

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private SuperheroService superheroService;

    @Scheduled(fixedRate = 10000)
    public void scheduledConsumeSuperhero() {
        consumeSuperhero();
    }

    public void consumeSuperhero() {
        try {
            ReceiveMessageResult receiveResult = amazonSQS.receiveMessage(new ReceiveMessageRequest()
                    .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue")
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(10));

            if (receiveResult.getMessages().isEmpty()) {
                System.out.println("No messages available in the queue.");
                return;
            }

            receiveResult.getMessages().forEach(message -> {
                try {
                    String messageBody = message.getBody();
                    System.out.println("Received message: " + messageBody);

                    // Parse the JSON message to extract fields
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> messageMap = objectMapper.readValue(messageBody, Map.class);
                    String superHeroName = messageMap.get("superHeroName");
                    String universe = messageMap.get("universe");
                    String power = messageMap.get("power");

                    // Check if the superhero exists in the database
                    Superhero existingSuperhero = superheroService.getByName(superHeroName);

                    if (existingSuperhero != null) {
                        // If superhero exists, create the request body for the update operation
                        SuperheroRequestBody updatedDetails = SuperheroRequestBody.builder()
                                .name(superHeroName)
                                .universe(universe)
                                .power(power)
                                .build();

                        // Update the superhero using the service
                        System.out.println("Superhero updated: ");
                        Superhero updatedSuperhero = superheroService.updateSuperhero(superHeroName, universe, updatedDetails);
                    } else {
                        // If superhero doesn't exist, log the information
                        System.out.println("Superhero with name " + superHeroName + " and universe " + universe + " not found in the database.");
                    }

                    amazonSQS.deleteMessage(new DeleteMessageRequest()
                            .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue")
                            .withReceiptHandle(message.getReceiptHandle()));

                    System.out.println("Deleted message from the queue: " + messageBody);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + message.getBody());

                }
            });
        } catch (Exception e) {
            System.err.println("Error consuming messages from the queue.");
        }
    }
}

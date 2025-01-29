package com.cleartax.training_superheroes.controllers;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.cleartax.training_superheroes.config.SqsConfig;
import com.cleartax.training_superheroes.dto.Superhero;
import com.cleartax.training_superheroes.dto.SuperheroRequestBody;
import com.cleartax.training_superheroes.services.SuperheroConsumer;
import com.cleartax.training_superheroes.services.SuperheroService;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.sqs.SqsClient;

@Data
@RestController
public class SuperheroController {

    private SuperheroService superheroService;
    private SqsConfig sqsconfig;
    private SqsClient sqsClient;
    private SuperheroConsumer superheroConsumer;
    private AmazonSQS amazonSQS;



    public SuperheroController(SuperheroService superheroService, AmazonSQS amazonSQS , SuperheroConsumer superheroConsumer) {
        this.superheroService = superheroService;
        this.amazonSQS = amazonSQS;
        this.superheroConsumer = superheroConsumer;
    }

    //OPERATION FOR SQS QUEUE

    @GetMapping("/hello")
    public String hello(
            @RequestParam(value = "superHeroName", defaultValue = "Hello") String superHeroName,
            @RequestParam(value = "universe", defaultValue = "World") String universe) {

        // Create a JSON-like message body
        String messageBody = String.format("{\"superHeroName\":\"%s\", \"universe\":\"%s\"}", superHeroName, universe);

        // Send the message to LocalStack
        amazonSQS.sendMessage(new com.amazonaws.services.sqs.model.SendMessageRequest()
                .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue") // Your LocalStack queue URL
                .withMessageBody(messageBody)); // Correct JSON format message body

        return String.format("The superHeroName %s from %s universe!", superHeroName, universe);
    }

    //hit the api to start the Scheduleder in superHeroconsumer;
    @GetMapping("/consume-superhero")
    public String manuallyConsumeSuperhero() {
        try {
            superheroConsumer.consumeSuperhero();
            return "Superhero messages consumed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error consuming superhero messages: " + e.getMessage();
        }
    }

    @GetMapping("/update_superhero_async")
    public String updateSuperhero(@RequestParam(value = "superHeroName", defaultValue = "ironMan") String superHeroName) {
        AmazonSQS amazonSQS = AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566", "ap-south-1"))  // LocalStack endpoint
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("accessKey", "secretKey")))  // LocalStack credentials
                .build();

        // Create SendMessageRequest using SDK v1 syntax
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl("http://sqs.ap-south-1.localhost.localstack.cloud:4566/000000000000/superhero-queue")  // Your LocalStack queue URL
                .withMessageBody(superHeroName);  // Message body is the superhero name

        // Send the message to the queue
        SendMessageResult result = amazonSQS.sendMessage(sendMessageRequest);
        return String.format("Message sent to queue with message id %s and superHero %s", result.getMessageId(), superHeroName);
    }


    //SIMPLE CRUD OPERATION FOR DATABASE

    @GetMapping("/superhero")
    public Superhero getSuperhero(@RequestParam(value = "name", defaultValue = "Batman") String name,
                                  @RequestParam(value = "universe", defaultValue = "DC") String universe){
        System.out.println("Fetching superhero with name: " + name + ", universe: " + universe);
        if (name == null && universe == null) {
            throw new IllegalArgumentException("At least one of 'name' or 'universe' must be provided");
        }

        return superheroService.getSuperhero(name, universe);
    }



    @PostMapping("/superhero")
    public Superhero persistSuperhero(@RequestBody SuperheroRequestBody superherorequestBody){
        System.out.println("Superhero " + superherorequestBody.getName() + " added in " + superherorequestBody.getUniverse() + " universe");
        return superheroService.persistSuperhero(superherorequestBody);
    }

    @DeleteMapping("/superhero")
    public String deleteSuperhero(@RequestParam(value = "name") String name,
                                  @RequestParam(value = "universe") String universe) {
        System.out.println("Deleting superhero: " + name + " from " + universe);
        boolean isDeleted = superheroService.deleteSuperhero(name, universe);

        if (isDeleted) {
            return String.format("Superhero %s from %s has been deleted successfully.", name, universe);
        } else {
            return String.format("Superhero %s from %s not found.", name, universe);
        }
    }
    @PutMapping("/superhero")
    public Superhero updateSuperhero(@RequestParam(value = "name") String name,
                                     @RequestParam(value = "universe") String universe,
                                     @RequestBody SuperheroRequestBody updatedDetails) {
        System.out.println("Updating superhero: " + name + " in universe: " + universe);
        return superheroService.updateSuperhero(name, universe, updatedDetails);
    }


}

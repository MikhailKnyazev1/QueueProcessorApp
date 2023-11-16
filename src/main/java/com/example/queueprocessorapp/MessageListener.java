package com.example.queueprocessorapp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

@Component
public class MessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Value("${rabbitmq.input.queue}")
    private String inputQueue;

    @Value("${rabbitmq.output.exchange}")
    private String outputExchange;

    @Value("${rabbitmq.output.routingkey}")
    private String outputRoutingKey;

    @Value("${message.processing.status}")
    private String processingStatus;

    @RabbitListener(queues = "#{@inputQueue}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);

        try {
            CustomMessage customMessage = mapper.readValue(message, CustomMessage.class);

            customMessage.setHandledTimestamp(Instant.now().toEpochMilli());
            customMessage.setStatus(processingStatus);

            String modifiedMessage = mapper.writeValueAsString(customMessage);

            rabbitTemplate.convertAndSend(outputExchange, outputRoutingKey, modifiedMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

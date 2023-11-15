package com.example.queueprocessorapp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;

@Component
public class MessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "test.input")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Переделываем JSON в CustomMessage
            CustomMessage customMessage = mapper.readValue(message, CustomMessage.class);

            // Добавляем дополнительные поля
            customMessage.setHandledTimestamp(Instant.now().toEpochMilli());
            customMessage.setStatus("Complete");

            // Переделываем обратно в JSON
            String modifiedMessage = mapper.writeValueAsString(customMessage);

            // Отправка измененного сообщения в RabbitMQ
            rabbitTemplate.convertAndSend("test.output", "output", modifiedMessage);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}

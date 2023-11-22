package com.example.queueprocessorapp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MyCamelRouter extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(MyCamelRouter.class);

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Override
    public void configure() {
        from("spring-rabbitmq:?connectionFactory=#rabbitConnectionFactory&queues=test.input")
                .routeId("ProcessAndSendRoute")
                .log("Received message from test.input: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, CustomMessage.class)
                .process(exchange -> {
                    CustomMessage message = exchange.getIn().getBody(CustomMessage.class);
                    String type = exchange.getIn().getHeader("type", String.class);

                    log.info("Processing message with type: " + type);

                    if ("employee".equals(type)) {
                        message.setHandledTimestamp(System.currentTimeMillis());
                        message.setStatus("Complete");
                        exchange.getMessage().setBody(message);
                    } else {
                        log.warn("Received non-employee type message");
                        exchange.getMessage().setHeader("type", "non-employee");
                    }
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("spring-rabbitmq:exchange.test.output?connectionFactory=#rabbitConnectionFactory&exchangeType=direct&routingKey=output")
                .log("Sent message to test.output: ${body}")
                .end();

        // Логирование ошибок
        errorHandler(
                deadLetterChannel("log:dlc?level=ERROR&showStackTrace=true")
        );
    }
}

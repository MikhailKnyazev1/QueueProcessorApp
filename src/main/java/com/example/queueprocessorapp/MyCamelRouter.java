package com.example.queueprocessorapp;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRouter extends RouteBuilder {

    @Override
    public void configure() {
        // Обработка сообщений из очереди и отправка в обменник
        from("rabbitmq:test.input") // Здесь указывается имя очереди
                .routeId("ProcessAndSendRoute")
                .log("Received message from test.input: ${body}")
                .unmarshal().json(JsonLibrary.Jackson, CustomMessage.class)
                .process(exchange -> {
                    CustomMessage message = exchange.getIn().getBody(CustomMessage.class);
                    String type = exchange.getIn().getHeader("type", String.class);

                    if ("employee".equals(type)) {
                        message.setHandledTimestamp(System.currentTimeMillis());
                        message.setStatus("Complete");
                        exchange.getMessage().setBody(message);
                        exchange.getMessage().setHeader("type", type);
                    } else {
                        // Логирование для других типов
                        exchange.getMessage().setHeader("type", "non-employee");
                    }
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("rabbitmq:test.output?routingKey=output") // Здесь указывается имя обменника и ключ маршрутизации
                .log("Sent message to test.output: ${body}")
                .end();

        // Логирование ошибок
        errorHandler(
                deadLetterChannel("log:dlc?level=ERROR&showStackTrace=true")
        );
    }
}

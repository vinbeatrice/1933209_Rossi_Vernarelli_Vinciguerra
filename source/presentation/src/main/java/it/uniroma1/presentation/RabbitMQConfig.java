package it.uniroma1.presentation;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "mars.events";
    public static final String PRESENTATION_QUEUE = "presentation.sensor-events";
    public static final String ROUTING_KEY = "sensor.reading";

    @Bean
    public TopicExchange sensorExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue presentationQueue() {
        return new Queue(PRESENTATION_QUEUE);
    }

    @Bean
    public Binding presentationBinding(Queue presentationQueue, TopicExchange sensorExchange) {
        return BindingBuilder.bind(presentationQueue)
                .to(sensorExchange)
                .with(ROUTING_KEY);
    }
}
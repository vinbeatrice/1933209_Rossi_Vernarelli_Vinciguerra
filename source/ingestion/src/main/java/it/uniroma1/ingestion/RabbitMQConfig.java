package it.uniroma1.ingestion;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "mars.events";
    public static final String PROCESSING_QUEUE = "processing.sensor-events";
    public static final String ROUTING_KEY = "sensor.reading";

    @Bean
    public TopicExchange sensorExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue processingQueue() {
        return new Queue(PROCESSING_QUEUE);
    }

    @Bean
    public Binding processingBinding(Queue processingQueue, TopicExchange sensorExchange) {
        return BindingBuilder.bind(processingQueue)
                .to(sensorExchange)
                .with(ROUTING_KEY);
    }
}
package it.uniroma1.processing;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SENSOR_EXCHANGE_NAME = "mars.events";
    public static final String PROCESSING_QUEUE = "processing.sensor-events";
    public static final String SENSOR_ROUTING_KEY = "sensor.reading";

    public static final String NOTIFICATION_EXCHANGE_NAME = "mars.notifications";
    public static final String PRESENTATION_RULE_QUEUE = "presentation.rule-events";
    public static final String RULE_TRIGGERED_ROUTING_KEY = "rue.triggered";


    @Bean
    public TopicExchange sensorExchange() {
        return new TopicExchange(SENSOR_EXCHANGE_NAME);
    }

    @Bean
    public Queue processingQueue() {
        return new Queue(PROCESSING_QUEUE, true);
    }

    @Bean
    public Binding processingBinding(Queue processingQueue, TopicExchange sensorExchange) {
        return BindingBuilder.bind(processingQueue)
                .to(sensorExchange)
                .with(SENSOR_ROUTING_KEY);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    public Queue presentationRuleQueue() {
        return new Queue(PRESENTATION_RULE_QUEUE, true);
    }

    @Bean
    public Binding presentationRuleBinding(Queue presentationRuleQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(presentationRuleQueue)
                .to(notificationExchange)
                .with(RULE_TRIGGERED_ROUTING_KEY);
    }
}
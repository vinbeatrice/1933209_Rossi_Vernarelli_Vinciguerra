package it.uniroma1.presentation;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SENSOR_EXCHANGE_NAME = "mars.events";
    public static final String PRESENTATION_QUEUE = "presentation.sensor-events";
    public static final String SENSOR_ROUTING_KEY = "sensor.reading";

    public static final String NOTIFICATION_EXCHANGE_NAME = "mars.notifications";
    public static final String PRESENTATION_RULE_QUEUE = "presentation.rule-events";
    public static final String RULE_TRIGGERED_ROUTING_KEY = "rue.triggered";

    public static final String PRESENTATION_ACTUATOR_QUEUE = "presentation.actuator-events";
    public static final String ACTUATOR_UPDATED_ROUTING_KEY = "actuator.updated";

    @Bean
    public TopicExchange sensorExchange() {
        return new TopicExchange(SENSOR_EXCHANGE_NAME);
    }

    @Bean
    public Queue presentationQueue() {
        return new Queue(PRESENTATION_QUEUE, true);
    }

    @Bean
    public Binding presentationBinding(Queue presentationQueue, TopicExchange sensorExchange) {
        return BindingBuilder.bind(presentationQueue)
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

    @Bean
    public Queue presentationActuatorQueue() {
        return new Queue(PRESENTATION_ACTUATOR_QUEUE, true);
    }

    @Bean
    public Binding presentationActuatorBinding(Queue presentationActuatorQueue,
                                            TopicExchange notificationExchange) {
        return BindingBuilder.bind(presentationActuatorQueue)
                .to(notificationExchange)
                .with(ACTUATOR_UPDATED_ROUTING_KEY);
    }
}
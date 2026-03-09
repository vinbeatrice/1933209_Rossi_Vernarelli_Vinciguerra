package it.uniroma1.processing.actuators_helpers;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import it.uniroma1.processing.RabbitMQConfig;

@Service
public class ActuatorEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ActuatorEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String actuatorName, String state) {
        Map<String, String> payload = Map.of(
                "actuatorName", actuatorName,
                "state", state
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                "actuator.updated",
                payload
        );
    }
}
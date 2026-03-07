package it.uniroma1.ingestion;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import it.uniroma1.ingestion.normalization_helpers.NormalizedEvent;

@Service
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishSensorEvent(NormalizedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        System.out.println("Published event for sensor: " + event.getSourceId());
    }
}
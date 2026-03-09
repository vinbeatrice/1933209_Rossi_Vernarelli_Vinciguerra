package it.uniroma1.processing;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RuleTriggeredEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RuleTriggeredEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(RuleTriggeredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                RabbitMQConfig.RULE_TRIGGERED_ROUTING_KEY,
                event
        );

        System.out.println("Published rule-triggered event for rule " + event.getRuleId());
    }
}
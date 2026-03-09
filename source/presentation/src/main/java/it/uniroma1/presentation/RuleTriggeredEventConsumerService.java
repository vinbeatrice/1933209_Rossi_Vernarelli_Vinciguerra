package it.uniroma1.presentation;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RuleTriggeredEventConsumerService {

    private final RuleNotificationCacheService ruleNotificationCacheService;
    private final SensorSseService sensorSseService;

    public RuleTriggeredEventConsumerService(RuleNotificationCacheService ruleNotificationCacheService,
                                             SensorSseService sensorSseService) {
        this.ruleNotificationCacheService = ruleNotificationCacheService;
        this.sensorSseService = sensorSseService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRESENTATION_RULE_QUEUE)
    public void receiveRuleTriggeredEvent(RuleTriggeredEvent event) {
        System.out.println("Presentation received rule-triggered event for rule: " + event.getRuleId());

        ruleNotificationCacheService.update(event);

        sensorSseService.broadcastRuleTriggered(event);
    }
}
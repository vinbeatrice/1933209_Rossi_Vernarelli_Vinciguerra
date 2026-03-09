package it.uniroma1.presentation;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RuleTriggeredEventConsumerService {

    private final RuleNotificationCacheService ruleNotificationCacheService;
    private final SensorSseService sensorSseService;
    private final ActuatorStateCacheService actuatorStateCacheService;

    public RuleTriggeredEventConsumerService(RuleNotificationCacheService ruleNotificationCacheService,
                                             SensorSseService sensorSseService,
                                             ActuatorStateCacheService actuatorStateCacheService) {
        this.ruleNotificationCacheService = ruleNotificationCacheService;
        this.sensorSseService = sensorSseService;
        this.actuatorStateCacheService = actuatorStateCacheService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRESENTATION_RULE_QUEUE)
    public void receiveRuleTriggeredEvent(RuleTriggeredEvent event) {
        System.out.println("Presentation received rule-triggered event for rule: " + event.getRuleId());

        ruleNotificationCacheService.update(event);
        actuatorStateCacheService.put(event.getActuatorName(), event.getTargetState());

        sensorSseService.broadcastRuleTriggered(event);
        sensorSseService.broadcastActuatorState(event.getActuatorName(), event.getTargetState());
    }
}
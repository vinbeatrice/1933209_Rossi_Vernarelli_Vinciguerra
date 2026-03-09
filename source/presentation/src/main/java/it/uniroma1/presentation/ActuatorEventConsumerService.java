package it.uniroma1.presentation;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ActuatorEventConsumerService {

    private final ActuatorStateCacheService actuatorStateCacheService;
    private final SensorSseService sensorSseService;

    public ActuatorEventConsumerService(ActuatorStateCacheService actuatorStateCacheService,
                                        SensorSseService sensorSseService) {
        this.actuatorStateCacheService = actuatorStateCacheService;
        this.sensorSseService = sensorSseService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRESENTATION_ACTUATOR_QUEUE)
    public void receiveActuatorUpdate(Map<String, String> payload) {
        String actuatorName = payload.get("actuatorName");
        String state = payload.get("state");

        if (actuatorName != null && state != null) {
            actuatorStateCacheService.put(actuatorName, state);
            sensorSseService.broadcastActuatorState(actuatorName, state);
        }
    }
}
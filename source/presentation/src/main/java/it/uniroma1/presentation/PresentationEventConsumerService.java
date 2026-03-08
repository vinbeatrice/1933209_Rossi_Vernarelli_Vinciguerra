package it.uniroma1.presentation;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import it.uniroma1.presentation.normalization_helpers.NormalizedEvent;

@Service
public class PresentationEventConsumerService {

    private final SensorCacheService sensorCacheService;
    private final SensorSseService sensorSseService;

    public PresentationEventConsumerService(SensorCacheService sensorCacheService,
                                            SensorSseService sensorSseService) {
        this.sensorCacheService = sensorCacheService;
        this.sensorSseService = sensorSseService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRESENTATION_QUEUE)
    public void receiveSensorEvent(NormalizedEvent event) {
        System.out.println("Presentation received event from: " + event.getSourceId());

        sensorCacheService.update(event);
        sensorSseService.broadcast(event);
    }
}
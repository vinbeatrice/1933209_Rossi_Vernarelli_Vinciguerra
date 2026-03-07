package it.uniroma1.ingestion;

import org.springframework.stereotype.Service;

import it.uniroma1.ingestion.normalization_helpers.NormalizedEvent;

@Service
public class SensorIngestionService {

    private final EventPublisherService eventPublisherService;

    public SensorIngestionService(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    public void forwardNormalizedEvent(NormalizedEvent event) {
        eventPublisherService.publishSensorEvent(event);
    }
}
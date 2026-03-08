package it.uniroma1.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.uniroma1.presentation.normalization_helpers.NormalizedEvent;

@RestController
public class SensorPresentationController {

    private final SensorCacheService sensorCacheService;
    private final SensorSseService sensorSseService;

    public SensorPresentationController(SensorCacheService sensorCacheService,
                                        SensorSseService sensorSseService) {
        this.sensorCacheService = sensorCacheService;
        this.sensorSseService = sensorSseService;
    }

    @GetMapping("/api/sensors/latest")
    public List<NormalizedEvent> getLatestSensors() {
        return sensorCacheService.getAllLatest();
    }

    @GetMapping("/api/sensors/history/{sensorId}")
    public List<NormalizedEvent> getHistory(@PathVariable String sensorId) {
        return sensorCacheService.getHistory(sensorId);
    }

    @GetMapping("/api/sensors/stream")
    public SseEmitter streamSensors() {
        return sensorSseService.subscribe();
    }
}
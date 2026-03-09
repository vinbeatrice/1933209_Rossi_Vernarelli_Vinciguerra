package it.uniroma1.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import it.uniroma1.presentation.normalization_helpers.NormalizedEvent;

@Service
public class SensorCacheService {

    private static final int MAX_HISTORY_SIZE = 20;

    private final Map<String, NormalizedEvent> latestBySensor = new ConcurrentHashMap<>();
    private final Map<String, List<NormalizedEvent>> historyBySensor = new ConcurrentHashMap<>();

    public void update(NormalizedEvent event) {
        String sensorId = event.getSourceId();

        latestBySensor.put(sensorId, event);

        historyBySensor.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(event);

        List<NormalizedEvent> history = historyBySensor.get(sensorId);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    public List<NormalizedEvent> getAllLatest() {
        return new ArrayList<>(latestBySensor.values());
    }

    public List<NormalizedEvent> getHistory(String sensorId) {
        return historyBySensor.getOrDefault(sensorId, Collections.emptyList());
    }

    public Map<String, List<NormalizedEvent>> getAllHistory(){
        return historyBySensor;
    }
}
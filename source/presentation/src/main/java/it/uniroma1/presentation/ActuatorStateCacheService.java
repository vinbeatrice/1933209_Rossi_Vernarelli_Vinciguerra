package it.uniroma1.presentation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ActuatorStateCacheService {

    private final Map<String, String> actuatorStates = new ConcurrentHashMap<>();

    public Map<String, String> getAll() {
        return actuatorStates;
    }

    public void put(String actuatorName, String state) {
        actuatorStates.put(actuatorName, state);
    }

    public void putAll(Map<String, String> states) {
        actuatorStates.putAll(states);
    }
}
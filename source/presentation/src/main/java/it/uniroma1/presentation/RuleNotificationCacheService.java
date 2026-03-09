package it.uniroma1.presentation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RuleNotificationCacheService {

    private volatile RuleTriggeredEvent latestTriggeredRule;
    private final Map<String, String> actuatorStateCache = new ConcurrentHashMap<>();

    public void update(RuleTriggeredEvent event) {
        this.latestTriggeredRule = event;
        actuatorStateCache.put(event.getActuatorName(), event.getTargetState());
    }

    public RuleTriggeredEvent getLatestTriggeredRule() {
        return latestTriggeredRule;
    }

    public Map<String, String> getActuatorStateCache() {
        return actuatorStateCache;
    }
}
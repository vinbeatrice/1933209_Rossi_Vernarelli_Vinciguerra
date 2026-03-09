package it.uniroma1.presentation;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorStateController {

    private final ActuatorStateCacheService actuatorStateCacheService;

    public ActuatorStateController(ActuatorStateCacheService actuatorStateCacheService) {
        this.actuatorStateCacheService = actuatorStateCacheService;
    }

    @GetMapping("/api/actuators/latest")
    public Map<String, String> getLatestActuatorStates() {
        return actuatorStateCacheService.getAll();
    }
}
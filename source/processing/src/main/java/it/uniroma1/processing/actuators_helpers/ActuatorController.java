package it.uniroma1.processing.actuators_helpers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorController {

    private final ActuatorService actuatorService;
    private final ActuatorEventPublisher actuatorEventPublisher;

    public ActuatorController(ActuatorService actuatorService,
                              ActuatorEventPublisher actuatorEventPublisher) {
        this.actuatorService = actuatorService;
        this.actuatorEventPublisher = actuatorEventPublisher;
    }

    @GetMapping("/actuators")
    public Map<String, String> getAllActuators() {
        return actuatorService.getAllActuators();
    }

    @PostMapping("/actuators/{actuatorName}")
    public void setActuatorState(@PathVariable String actuatorName,
                                 @RequestBody ActuatorStateRequest request) {
        actuatorService.setActuatorState(actuatorName, request.getState());
        actuatorEventPublisher.publish(actuatorName, request.getState());
    }
}
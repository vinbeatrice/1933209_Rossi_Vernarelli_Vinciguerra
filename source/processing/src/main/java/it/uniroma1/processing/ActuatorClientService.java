package it.uniroma1.processing;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ActuatorClientService {

    @Value("${simulator.base-url}")
    private String simulatorBaseUrl;

    private final RestTemplate restTemplate;

    public ActuatorClientService() {
        this.restTemplate = new RestTemplate();
    }

    public void apply(String actuatorName, String targetState) {
        String currentState = getCurrentState(actuatorName);

        if (currentState != null && currentState.equalsIgnoreCase(targetState)) {
            System.out.println("Skipping actuator update: " + actuatorName +
                    " is already " + targetState);
            return;
        }

        String url = simulatorBaseUrl + "/api/actuators/" + actuatorName;
        Map<String, String> body = Map.of("state", targetState);

        restTemplate.postForEntity(url, body, Void.class);

        System.out.println("Actuator updated: " + actuatorName + " -> " + targetState);
    }

    public String getCurrentState(String actuatorName) {
        String url = simulatorBaseUrl + "/api/actuators";

        ResponseEntity<ActuatorsResponse> response =
                restTemplate.getForEntity(url, ActuatorsResponse.class);

        ActuatorsResponse actuatorsResponse = response.getBody();

        if (actuatorsResponse == null || actuatorsResponse.getActuators() == null) {
            return null;
        }

        return actuatorsResponse.getActuators().get(actuatorName);
    }
}
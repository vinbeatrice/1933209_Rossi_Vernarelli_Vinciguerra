package it.uniroma1.processing.actuators_helpers;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ActuatorService {

    @Value("${simulator.base-url}")
    private String simulatorBaseUrl;

    private final RestTemplate restTemplate;

    public ActuatorService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, String> getAllActuators() {
        String url = simulatorBaseUrl + "/api/actuators";

        ResponseEntity<ActuatorsResponse> response =
                restTemplate.getForEntity(url, ActuatorsResponse.class);

        ActuatorsResponse body = response.getBody();

        if (body == null || body.getActuators() == null) {
            return Collections.emptyMap();
        }

        return body.getActuators();
    }

    public void setActuatorState(String actuatorName, String targetState) {
        String url = simulatorBaseUrl + "/api/actuators/" + actuatorName;

        ActuatorStateRequest request = new ActuatorStateRequest(targetState);

        restTemplate.postForEntity(url, request, Void.class);
    }
}
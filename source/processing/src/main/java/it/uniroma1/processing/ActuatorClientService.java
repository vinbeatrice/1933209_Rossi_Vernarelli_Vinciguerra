package it.uniroma1.processing;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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
        String url = simulatorBaseUrl + "/api/actuators/" + actuatorName;

        Map<String, String> body = Map.of("state", targetState);

        restTemplate.postForEntity(url, body, Void.class);

        System.out.println("Actuator updated: " + actuatorName + " -> " + targetState);
    }
}
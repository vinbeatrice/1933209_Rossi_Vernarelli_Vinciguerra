package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class DiscoveryClient {
    private static final String BASE_URL = "http://simulator:8080/api";
    private final RestPollingService restPollingService;
    private final TelemetrySubscriberService telemetrySubscriberService;
    private final RestPollingConfigService restPollingConfigService;

    public DiscoveryClient(RestPollingService restPollingService, TelemetrySubscriberService telemetrySubscriberService, RestPollingConfigService restPollingConfigService) {
        this.restPollingService = restPollingService;
        this.telemetrySubscriberService = telemetrySubscriberService;
        this.restPollingConfigService = restPollingConfigService;

    }

    @GetMapping("/discovery/rest_sensors")
    public List<RestSensor> getDiscoveryRest_sensors() throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(BASE_URL + "/discovery");
        
        try(InputStream input = url.openStream()){
        
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            return dr.getRest_sensors();
        }
    }
    
    @GetMapping("/discovery/telemetry_streams")
    public List<PubsubStream> getDiscoveryPubsub_streams() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(BASE_URL + "/discovery");

        try(InputStream input = url.openStream()){
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);
            return dr.getPubsub_streams();
        }
    }

    @GetMapping("/discovery/rest_sensors/{sensor_id}")
    public NormalizedEvent getNormalizedRestSensor(@PathVariable("sensor_id") String sensorId) throws Exception {

        RestSensor sensor = findRestSensorById(sensorId);

        return restPollingService.fetchAndNormalizeRestSensor(sensor);
    }

    public RestSensor findRestSensorById(String sensorId) throws Exception {
        URL discoveryUrl = new URL(BASE_URL + "/discovery");
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = discoveryUrl.openStream()) {
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            return dr.getRest_sensors().stream()
                    .filter(sensor -> sensor.getSensor_id().equals(sensorId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
        }
    }

    @PostMapping("/api/rest-sensors/{sensorId}/refresh")
    public NormalizedEvent refreshSingleRestSensor(@PathVariable String sensorId) throws Exception {
        return restPollingService.refreshSingleRestSensor(sensorId);
    }

    @GetMapping("/api/rest-sensors/polling-interval")
    public Map<String, Long> getPollingInterval() {
        return Map.of("pollingDelayMs", restPollingConfigService.getPollingDelayMs());
    }

    @PutMapping("/api/rest-sensors/polling-interval")
    public Map<String, Long> updatePollingInterval(@RequestBody Map<String, Long> body) {
        Long newDelay = body.get("pollingDelayMs");

        if (newDelay == null) {
            throw new IllegalArgumentException("Missing pollingDelayMs");
        }

        restPollingConfigService.setPollingDelayMs(newDelay);

        return Map.of("pollingDelayMs", restPollingConfigService.getPollingDelayMs());
    }


    @GetMapping("/discovery/telemetry/{*topic}")
    public SseEmitter streamNormalizedTelemetry(@PathVariable("topic") String topic) {
        if (topic.startsWith("/")) {
            topic = topic.substring(1);
        }

        return telemetrySubscriberService.streamNormalizedTelemetry(topic);
    }

}

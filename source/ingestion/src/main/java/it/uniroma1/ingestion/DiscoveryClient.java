package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class DiscoveryClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final NormalizerRegistry normalizerRegistry;
    private final RestPollingService restPollingService;
    private final TelemetrySubscriberService telemetrySubscriberService;

    public DiscoveryClient(NormalizerRegistry normalizerRegistry, RestPollingService restPollingService, TelemetrySubscriberService telemetrySubscriberService) {
        this.normalizerRegistry = normalizerRegistry;
        this.restPollingService = restPollingService;
        this.telemetrySubscriberService = telemetrySubscriberService;

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
    /*
    public NormalizedEvent getNormalizedRestSensor(@PathVariable("sensor_id") String sensorId) throws Exception {

        // 1. Recupero la lista dei sensori dalla discovery
        URL discoveryUrl = new URL(BASE_URL + "/discovery");
        RestSensor targetSensor;
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = discoveryUrl.openStream()) {
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            targetSensor = dr.getRest_sensors().stream()
                    .filter(sensor -> sensor.getSensor_id().equals(sensorId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
        }

        // 2. Recupero lo schema del sensore trovato
        String schema = targetSensor.getSchema_id();

        // 3. Chiamo l'endpoint del sensore
        URL sensorUrl = new URL(BASE_URL + "/sensors/" + sensorId);
        JsonNode payload;

        try (InputStream input = sensorUrl.openStream()) {
            payload = mapper.readTree(input);
        }

        // 4. Normalizzo il payload
        NormalizedEvent normalizedEvent = normalizerRegistry.normalize(schema, payload);

        // 5. Stampo in terminale
        System.out.println("Normalized payload for sensor " + sensorId + ":");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(normalizedEvent));

        // 6. Lo restituisco come risposta HTTP
        return normalizedEvent;
    }*/
   public NormalizedEvent getNormalizedRestSensor(@PathVariable("sensor_id") String sensorId) throws Exception {

        RestSensor sensor = findRestSensorById(sensorId);

        return restPollingService.fetchAndNormalizeRestSensor(sensor);
    }

    private RestSensor findRestSensorById(String sensorId) throws Exception {
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


    @GetMapping("/discovery/telemetry/{*topic}")
    public SseEmitter streamNormalizedTelemetry(@PathVariable("topic") String topic) {
        if (topic.startsWith("/")) {
            topic = topic.substring(1);
        }

        return telemetrySubscriberService.streamNormalizedTelemetry(topic);
    }

}

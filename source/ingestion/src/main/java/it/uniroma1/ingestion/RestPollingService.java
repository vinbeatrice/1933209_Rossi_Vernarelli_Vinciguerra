package it.uniroma1.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.uniroma1.ingestion.normalization_helpers.NormalizedEvent;
import it.uniroma1.ingestion.normalization_helpers.RestSensor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class RestPollingService {

    private static final String BASE_URL = "http://simulator:8080/api/sensors/";
    private static final String DR_URL = "http://simulator:8080/api";

    private final NormalizerRegistry normalizerRegistry;
    private final SensorIngestionService sensorIngestionService;

    private final ObjectMapper mapper = new ObjectMapper();

    public RestPollingService(
            NormalizerRegistry normalizerRegistry,
            SensorIngestionService sensorIngestionService
    ) {
        this.normalizerRegistry = normalizerRegistry;
        this.sensorIngestionService = sensorIngestionService;
    }

    public RestSensor findRestSensorById(String sensorId) throws Exception {
        URL discoveryUrl = new URL(DR_URL + "/discovery");
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = discoveryUrl.openStream()) {
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            return dr.getRest_sensors().stream()
                    .filter(sensor -> sensor.getSensor_id().equals(sensorId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sensor not found: " + sensorId));
        }
    }

    public NormalizedEvent refreshSingleRestSensor(String sensorId) throws Exception {
        RestSensor sensor = findRestSensorById(sensorId);
        NormalizedEvent event = fetchAndNormalizeRestSensor(sensor);
        sensorIngestionService.forwardNormalizedEvent(event);

        return event;
    }


    public NormalizedEvent fetchAndNormalizeRestSensor(RestSensor sensor) throws Exception {
        String sensorId = sensor.getSensor_id();
        String schema = sensor.getSchema_id();

        URL sensorUrl = new URL(BASE_URL + sensorId);

        JsonNode payload;
        try (InputStream input = sensorUrl.openStream()) {
            payload = mapper.readTree(input);
        }

        return normalizerRegistry.normalize(schema, payload);
    }

    public void processSingleRestSensor(RestSensor sensor) {
        try {
            NormalizedEvent event = fetchAndNormalizeRestSensor(sensor);
            System.out.println("Fetched REST sensor: " + sensor.getSensor_id() + " with data: " + event);
            sensorIngestionService.forwardNormalizedEvent(event);
        } catch (Exception e) {
            System.err.println("Errore durante il processing del sensore REST "
                    + sensor.getSensor_id() + ": " + e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${ingestion.rest.polling-delay-ms:10000}")
    public void pollAllRestSensors() {
        try {
            URL discoveryUrl = new URL(DR_URL + "/discovery");

            try (InputStream input = discoveryUrl.openStream()) {
                DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);
                List<RestSensor> sensors = dr.getRest_sensors();

                for (RestSensor sensor : sensors) {
                    processSingleRestSensor(sensor);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore durante Rest Polling Discovery: " + e.getMessage());
        }
    }
}
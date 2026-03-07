package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URL;

@Service
public class RestPollingService {
    private static final String BASE_URL = "http://localhost:8080/api/sensors/";

    private final NormalizerRegistry normalizerRegistry;
    private final ObjectMapper mapper = new ObjectMapper();

    public RestPollingService(NormalizerRegistry normalizerRegistry) {
        this.normalizerRegistry = normalizerRegistry;
    }
    /*
    public void handleSensorResponse(String schema, String jsonResponse) throws Exception {

        JsonNode payload = objectMapper.readTree(jsonResponse);

        NormalizedEvent event = normalizerRegistry.normalize(schema, payload);

        System.out.println("Normalized event: " + event);
    }*/

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
}
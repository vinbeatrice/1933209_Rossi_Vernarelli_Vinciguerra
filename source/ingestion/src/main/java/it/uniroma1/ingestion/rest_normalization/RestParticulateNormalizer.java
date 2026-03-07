package it.uniroma1.ingestion.rest_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class RestParticulateNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "rest.particulate.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode rawPayload) {
        String sensorId = rawPayload.get("sensor_id").asText();
        String timestamp = rawPayload.get("captured_at").asText();
        String status = rawPayload.get("status").asText();

        List<Measurement> measurements = List.of(
                new Measurement("pm1", rawPayload.get("pm1_ug_m3").asDouble(), "ug/m3"),
                new Measurement("pm25", rawPayload.get("pm25_ug_m3").asDouble(), "ug/m3"),
                new Measurement("pm10", rawPayload.get("pm10_ug_m3").asDouble(), "ug/m3")
        );

        return new NormalizedEvent(
                sensorId,
                SourceType.REST,
                "rest.particulate.v1",
                timestamp,
                status,
                measurements
        );
    }
}
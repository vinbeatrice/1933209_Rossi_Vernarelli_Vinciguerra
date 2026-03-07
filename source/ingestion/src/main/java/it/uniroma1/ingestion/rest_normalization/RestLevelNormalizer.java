package it.uniroma1.ingestion.rest_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class RestLevelNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "rest.level.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode rawPayload) {
        String sensorId = rawPayload.get("sensor_id").asText();
        String timestamp = rawPayload.get("captured_at").asText();
        String status = rawPayload.get("status").asText();

        List<Measurement> measurements = List.of(
                new Measurement("level_pct", rawPayload.get("level_pct").asDouble(), "%"),
                new Measurement("level_liters", rawPayload.get("level_liters").asDouble(), "L")
        );

        return new NormalizedEvent(
                sensorId,
                SourceType.REST,
                "rest.level.v1",
                timestamp,
                status,
                measurements
        );
    }
}
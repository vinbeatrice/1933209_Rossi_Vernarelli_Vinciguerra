package it.uniroma1.ingestion.rest_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class RestScalarNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "rest.scalar.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode rawPayload) {
        String sensorId = rawPayload.get("sensor_id").asText();
        String timestamp = rawPayload.get("captured_at").asText();
        String status = rawPayload.get("status").asText();

        String metric = rawPayload.get("metric").asText();
        Double value = rawPayload.get("value").asDouble();
        String unit = rawPayload.get("unit").asText();

        Measurement measurement = new Measurement(metric, value, unit);

        return new NormalizedEvent(
                sensorId,
                SourceType.REST,
                "rest.scalar.v1",
                timestamp,
                status,
                List.of(measurement)
        );
    }
}
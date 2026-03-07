package it.uniroma1.ingestion.rest_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class RestChemistryNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "rest.chemistry.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode rawPayload) {
        String sensorId = rawPayload.get("sensor_id").asText();
        String timestamp = rawPayload.get("captured_at").asText();
        String status = rawPayload.get("status").asText();

        List<Measurement> measurements = new ArrayList<>();
        for (JsonNode m : rawPayload.get("measurements")) {
            measurements.add(new Measurement(
                    m.get("metric").asText(),
                    m.get("value").asDouble(),
                    m.get("unit").asText()
            ));
        }

        return new NormalizedEvent(
                sensorId,
                SourceType.REST,
                "rest.chemistry.v1",
                timestamp,
                status,
                measurements
        );
    }
}
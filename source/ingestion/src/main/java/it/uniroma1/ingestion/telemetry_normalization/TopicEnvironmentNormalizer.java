package it.uniroma1.ingestion.telemetry_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicEnvironmentNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "topic.environment.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode payload) {
        List<Measurement> measurements = new ArrayList<>();

        JsonNode measurementsNode = payload.path("measurements");
        if (measurementsNode.isArray()) {
            for (JsonNode m : measurementsNode) {
                measurements.add(new Measurement(
                        m.path("metric").asText(),
                        m.path("value").asDouble(),
                        m.path("unit").asText()
                ));
            }
        }

        NormalizedEvent event = new NormalizedEvent();
        event.setSourceId(payload.path("topic").asText());
        event.setSourceType(SourceType.STREAM);
        event.setSchema("topic.environment.v1");
        event.setTimestamp(payload.path("event_time").asText());
        event.setStatus(payload.path("status").asText());
        event.setMeasurements(measurements);

        return event;
    }
}
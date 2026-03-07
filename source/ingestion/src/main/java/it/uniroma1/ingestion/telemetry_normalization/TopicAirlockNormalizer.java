package it.uniroma1.ingestion.telemetry_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicAirlockNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "topic.airlock.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode payload) {
        List<Measurement> measurements = new ArrayList<>();

        measurements.add(new Measurement(
                "cycles_per_hour",
                payload.path("cycles_per_hour").asDouble(),
                "cycles/hour"
        ));

        // last_state non è numerico, quindi lo tratto come measurement testuale
        // solo se la vostra classe lo supporta. Se non lo supporta, vedi nota sotto.
        measurements.add(new Measurement(
                "last_state",
                null,
                payload.path("last_state").asText()
        ));

        NormalizedEvent event = new NormalizedEvent();
        event.setSourceId(payload.path("topic").asText());
        event.setSourceType(SourceType.STREAM);
        event.setSchema("topic.airlock.v1");
        event.setTimestamp(payload.path("event_time").asText());
        event.setStatus(null);
        event.setMeasurements(measurements);

        return event;
    }
}
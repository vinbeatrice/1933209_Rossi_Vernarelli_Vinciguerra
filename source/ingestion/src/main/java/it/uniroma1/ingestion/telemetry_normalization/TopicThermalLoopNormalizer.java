package it.uniroma1.ingestion.telemetry_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicThermalLoopNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "topic.thermal_loop.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode payload) {
        List<Measurement> measurements = new ArrayList<>();

        measurements.add(new Measurement(
                "temperature_c",
                payload.path("temperature_c").asDouble(),
                "C"
        ));

        measurements.add(new Measurement(
                "flow_l_min",
                payload.path("flow_l_min").asDouble(),
                "L/min"
        ));

        NormalizedEvent event = new NormalizedEvent();
        event.setSourceId(payload.path("topic").asText());
        event.setSourceType(SourceType.STREAM);
        event.setSchema("topic.thermal_loop.v1");
        event.setTimestamp(payload.path("event_time").asText());
        event.setStatus(payload.path("status").asText());
        event.setMeasurements(measurements);

        return event;
    }
}
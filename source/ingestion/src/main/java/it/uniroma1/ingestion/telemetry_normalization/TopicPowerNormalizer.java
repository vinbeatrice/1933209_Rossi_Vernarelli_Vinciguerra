package it.uniroma1.ingestion.telemetry_normalization;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopicPowerNormalizer implements SchemaNormalizer {

    @Override
    public boolean supports(String schema) {
        return "topic.power.v1".equals(schema);
    }

    @Override
    public NormalizedEvent normalize(JsonNode payload) {
        List<Measurement> measurements = new ArrayList<>();

        measurements.add(new Measurement(
                "power_kw",
                payload.path("power_kw").asDouble(),
                "kW"
        ));

        measurements.add(new Measurement(
                "voltage_v",
                payload.path("voltage_v").asDouble(),
                "V"
        ));

        measurements.add(new Measurement(
                "current_a",
                payload.path("current_a").asDouble(),
                "A"
        ));

        measurements.add(new Measurement(
                "cumulative_kwh",
                payload.path("cumulative_kwh").asDouble(),
                "kWh"
        ));

        NormalizedEvent event = new NormalizedEvent();
        event.setSourceId(payload.path("topic").asText());
        event.setSourceType(SourceType.STREAM);
        event.setSchema("topic.power.v1");
        event.setTimestamp(payload.path("event_time").asText());
        event.setStatus(null);
        event.setMeasurements(measurements);

        return event;
    }
}
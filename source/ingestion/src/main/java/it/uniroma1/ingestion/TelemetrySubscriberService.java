package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TelemetrySubscriberService {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final ObjectMapper mapper = new ObjectMapper();
    private final NormalizerRegistry normalizerRegistry;

    public TelemetrySubscriberService(NormalizerRegistry normalizerRegistry) {
        this.normalizerRegistry = normalizerRegistry;
    }

    private PubsubStream findStreamByTopic(String topic) throws Exception {
        URL discoveryUrl = new URL(BASE_URL + "/discovery");

        try (InputStream input = discoveryUrl.openStream()) {
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            return dr.getPubsub_streams().stream()
                    .filter(stream -> stream.getTopic().equals(topic))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Telemetry topic not found: " + topic));
        }
    }

    public SseEmitter streamNormalizedTelemetry(String topic) {
        String normalizedTopic = topic;
        if (normalizedTopic.startsWith("/")) {
            normalizedTopic = normalizedTopic.substring(1);
        }

        final String finalTopic = normalizedTopic;

        SseEmitter emitter = new SseEmitter(0L);

        new Thread(() -> {
            try {
                PubsubStream stream = findStreamByTopic(finalTopic);
                String schema = stream.getSchema_id();

                URL streamUrl = new URL(BASE_URL + "/telemetry/stream/" + finalTopic);

                try (InputStream input = streamUrl.openStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(input, StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data:")) {
                            String json = line.substring(5).trim();
                            JsonNode payload = mapper.readTree(json);

                            NormalizedEvent event = normalizerRegistry.normalize(schema, payload);

                            emitter.send(SseEmitter.event()
                                    .name("normalized-telemetry")
                                    .data(event));
                        }
                    }
                }

                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    
}
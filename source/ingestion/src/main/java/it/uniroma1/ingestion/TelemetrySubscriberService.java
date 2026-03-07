package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.normalization_helpers.PubsubStream;
import jakarta.annotation.PostConstruct;

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

    private static final String BASE_URL = "http://simulator:8080/api";

    private final ObjectMapper mapper = new ObjectMapper();
    private final NormalizerRegistry normalizerRegistry;
    private final SensorIngestionService sensorIngestionService;

    public TelemetrySubscriberService(
            NormalizerRegistry normalizerRegistry,
            SensorIngestionService sensorIngestionService
    ) {
        this.normalizerRegistry = normalizerRegistry;
        this.sensorIngestionService = sensorIngestionService;
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

    public NormalizedEvent handleTelemetryMessage(String schema, String jsonMessage) throws Exception {
        JsonNode payload = mapper.readTree(jsonMessage);
        return normalizerRegistry.normalize(schema, payload);
    }

    @PostConstruct
    public void startTelemetrySubscriptions() {
        try {
            URL discoveryUrl = new URL(BASE_URL + "/discovery");

            try (InputStream input = discoveryUrl.openStream()) {
                DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

                for (PubsubStream stream : dr.getPubsub_streams()) {
                    processTelemetryStream(stream.getTopic());
                }
            }

        } catch (Exception e) {
            System.err.println("Errore durante l'avvio delle sottoscrizioni telemetry: " + e.getMessage());
        }
    }

    public void processTelemetryStream(String topic) {
        String normalizedTopic = topic;
        if (normalizedTopic.startsWith("/")) {
            normalizedTopic = normalizedTopic.substring(1);
        }

        final String finalTopic = normalizedTopic;

        new Thread(() -> {
            while (true) {
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

                                NormalizedEvent event = handleTelemetryMessage(schema, json);
                                sensorIngestionService.forwardNormalizedEvent(event);
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Errore durante il processing dello stream telemetry "
                            + finalTopic + ": " + e.getMessage());
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }).start();
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

                            NormalizedEvent event = handleTelemetryMessage(schema, json);

                            sensorIngestionService.forwardNormalizedEvent(event);

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
package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NormalizerRegistry {

    private final List<SchemaNormalizer> normalizers;

    public NormalizerRegistry(List<SchemaNormalizer> normalizers) {
        this.normalizers = normalizers;
    }

    public NormalizedEvent normalize(String schema, JsonNode payload) {
        return normalizers.stream()
                .filter(n -> n.supports(schema))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No normalizer found for schema: " + schema))
                .normalize(payload);
    }
}
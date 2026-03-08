package it.uniroma1.presentation.normalization_helpers;


import com.fasterxml.jackson.databind.JsonNode;

public interface SchemaNormalizer {
    boolean supports(String schema);
    NormalizedEvent normalize(JsonNode rawPayload);
}
package it.uniroma1.ingestion.normalization_helpers;

import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import com.fasterxml.jackson.databind.JsonNode;

public interface SchemaNormalizer {
    boolean supports(String schema);
    NormalizedEvent normalize(JsonNode rawPayload);
}
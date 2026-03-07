package it.uniroma1.processing.normalization_helpers;

import java.util.List;

public class NormalizedEvent {
    private String sourceId;
    private SourceType sourceType;
    private String schema;
    private String timestamp;
    private String status;
    private List<Measurement> measurements;

    public NormalizedEvent() {}

    public NormalizedEvent(String sourceId, SourceType sourceType, String schema,
                           String timestamp, String status, List<Measurement> measurements) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.schema = schema;
        this.timestamp = timestamp;
        this.status = status;
        this.measurements = measurements;
    }

    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSchema() {
        return schema;
    }
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}
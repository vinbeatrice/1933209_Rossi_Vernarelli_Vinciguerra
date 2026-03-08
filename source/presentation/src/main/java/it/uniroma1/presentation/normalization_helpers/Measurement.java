package it.uniroma1.presentation.normalization_helpers;

public class Measurement {
    private String metric;
    private Double value;
    private String unit;

    public Measurement() {}

    public Measurement(String metric, Double value, String unit) {
        this.metric = metric;
        this.value = value;
        this.unit = unit;
    }

    public String getMetric() {
        return metric;
    }
    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
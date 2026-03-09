package it.uniroma1.presentation;

public class RuleTriggeredEvent {

    private Long ruleId;
    private String sensorName;
    private String metric;
    private String operator;
    private Double thresholdValue;
    private String unit;
    private String actuatorName;
    private String targetState;
    private String timestamp;

    public RuleTriggeredEvent() {
    }

    public RuleTriggeredEvent(Long ruleId,
                              String sensorName,
                              String metric,
                              String operator,
                              Double thresholdValue,
                              String unit,
                              String actuatorName,
                              String targetState,
                              String timestamp) {
        this.ruleId = ruleId;
        this.sensorName = sensorName;
        this.metric = metric;
        this.operator = operator;
        this.thresholdValue = thresholdValue;
        this.unit = unit;
        this.actuatorName = actuatorName;
        this.targetState = targetState;
        this.timestamp = timestamp;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getActuatorName() {
        return actuatorName;
    }

    public void setActuatorName(String actuatorName) {
        this.actuatorName = actuatorName;
    }

    public String getTargetState() {
        return targetState;
    }

    public void setTargetState(String targetState) {
        this.targetState = targetState;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
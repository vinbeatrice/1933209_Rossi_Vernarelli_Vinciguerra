package it.uniroma1.processing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "automation_rules")
public class AutomationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorName;

    // Ogni sensore ha vari valori, ciascuno riferito ad una metrica diversa.
    // Il campo measurement è una lista di triple, una per ciascuna metrica, con valore e unità.
    @Column(nullable = false)
    private String metric;

    @Column(nullable = false)
    private String operator;

    @Column(nullable = false)
    private Double thresholdValue;

    private String unit;

    @Column(nullable = false)
    private String actuatorName;

    // ON - OFF: stato in cui vogliamo che sia l'attuatore dopo l'esecuzione della regola.
    @Column(nullable = false)
    private String targetState;

    // True - False: si riferisce alla regola, se vogliamo che venga considerata tra le applicabili o no.
    @Column(nullable = false)
    private boolean enabled = true;

    private String description;

    public AutomationRule() {
    }

    public Long getId() {
        return id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
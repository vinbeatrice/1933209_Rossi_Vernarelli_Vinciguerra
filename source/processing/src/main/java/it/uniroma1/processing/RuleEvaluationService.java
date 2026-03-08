package it.uniroma1.processing;

import java.util.List;

import org.springframework.stereotype.Service;

import it.uniroma1.processing.normalization_helpers.NormalizedEvent;
import it.uniroma1.processing.normalization_helpers.Measurement;

@Service
public class RuleEvaluationService {

    private final AutomationRuleRepository automationRuleRepository;
    private final ActuatorClientService actuatorClientService;

    public RuleEvaluationService(AutomationRuleRepository automationRuleRepository,
                                 ActuatorClientService actuatorClientService) {
        this.automationRuleRepository = automationRuleRepository;
        this.actuatorClientService = actuatorClientService;
    }

    public void evaluate(NormalizedEvent event) {
        List<AutomationRule> rules =
                automationRuleRepository.findBySensorNameAndEnabledTrue(event.getSourceId());

        for (AutomationRule rule : rules) {
            System.out.println("Evaluating rule " + rule.getId());
            if (matches(rule, event)) {
                actuatorClientService.apply(rule.getActuatorName(), rule.getTargetState());
                System.out.println("Rule triggered: " + rule.getId());
            }
        }
    }

    private boolean matches(AutomationRule rule, NormalizedEvent event) {
        List<Measurement> sensorMeasurements = event.getMeasurements();
        String ruleMetric = rule.getMetric();
        double threshold = rule.getThresholdValue();
        String operator = rule.getOperator();
        double sensorValue;

        for (Measurement m : sensorMeasurements) {
            if (m.getMetric().equals(ruleMetric)) {
                sensorValue = m.getValue();
                System.out.println("Evaluating rule " + rule.getId() + ": " + sensorValue + " " + operator + " " + threshold);
                return switch (operator) {
                    case ">" -> sensorValue > threshold;
                    case ">=" -> sensorValue >= threshold;
                    case "<" -> sensorValue < threshold;
                    case "<=" -> sensorValue <= threshold;
                    case "=" -> sensorValue == threshold;
                    default -> false;
                };
            }
        }
        return false;
    }
}
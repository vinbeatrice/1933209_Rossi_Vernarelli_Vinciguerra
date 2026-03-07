package it.uniroma1.processing;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final AutomationRuleRepository ruleRepository;

    public RuleService(AutomationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<AutomationRule> getAllRules() {
        return ruleRepository.findAll();
    }

    public AutomationRule getRuleById(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id " + id));
    }

    public AutomationRule createRule(AutomationRule rule) {
        return ruleRepository.save(rule);
    }

    public AutomationRule updateRule(Long id, AutomationRule updatedRule) {
        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id " + id));

        rule.setSensorName(updatedRule.getSensorName());
        rule.setOperator(updatedRule.getOperator());
        rule.setThresholdValue(updatedRule.getThresholdValue());
        rule.setUnit(updatedRule.getUnit());
        rule.setActuatorName(updatedRule.getActuatorName());
        rule.setTargetState(updatedRule.getTargetState());
        rule.setEnabled(updatedRule.isEnabled());
        rule.setDescription(updatedRule.getDescription());

        return ruleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    public List<AutomationRule> getActiveRulesBySensor(String sensorName) {
        return ruleRepository.findBySensorNameAndEnabledTrue(sensorName);
    }

    public AutomationRule setEnabled(Long id, boolean enabled) {
        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id " + id));

        rule.setEnabled(enabled);
        return ruleRepository.save(rule);
    }
}
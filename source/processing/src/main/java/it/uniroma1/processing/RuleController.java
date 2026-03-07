package it.uniroma1.processing;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping
    public List<AutomationRule> getAllRules() {
        return ruleService.getAllRules();
    }

    @GetMapping("/{id}")
    public AutomationRule getRuleById(@PathVariable Long id) {
        return ruleService.getRuleById(id);
    }

    @PostMapping
    public AutomationRule createRule(@RequestBody AutomationRule rule) {
        return ruleService.createRule(rule);
    }

    @PutMapping("/{id}")
    public AutomationRule updateRule(@PathVariable Long id, @RequestBody AutomationRule rule) {
        return ruleService.updateRule(id, rule);
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
    }

    @PatchMapping("/{id}/enable")
    public AutomationRule enableRule(@PathVariable Long id) {
        return ruleService.setEnabled(id, true);
    }

    @PatchMapping("/{id}/disable")
    public AutomationRule disableRule(@PathVariable Long id) {
        return ruleService.setEnabled(id, false);
    }
}
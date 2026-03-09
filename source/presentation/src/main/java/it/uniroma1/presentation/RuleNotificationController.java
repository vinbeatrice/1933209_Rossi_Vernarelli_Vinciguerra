package it.uniroma1.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RuleNotificationController {

    private final RuleNotificationCacheService ruleNotificationCacheService;

    public RuleNotificationController(RuleNotificationCacheService ruleNotificationCacheService) {
        this.ruleNotificationCacheService = ruleNotificationCacheService;
    }

    @GetMapping("/api/rules/latest-triggered")
    public RuleTriggeredEvent getLatestTriggeredRule() {
        return ruleNotificationCacheService.getLatestTriggeredRule();
    }
}
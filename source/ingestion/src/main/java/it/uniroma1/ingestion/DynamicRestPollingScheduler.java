package it.uniroma1.ingestion;

import java.time.Instant;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class DynamicRestPollingScheduler implements SchedulingConfigurer {

    private final RestPollingService restPollingService;
    private final RestPollingConfigService restPollingConfigService;

    public DynamicRestPollingScheduler(RestPollingService restPollingService,
                                       RestPollingConfigService restPollingConfigService) {
        this.restPollingService = restPollingService;
        this.restPollingConfigService = restPollingConfigService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        restPollingService.pollAllRestSensors();
                    } catch (Exception e) {
                        System.err.println("Error while polling REST sensors: " + e.getMessage());
                    }
                },
                new Trigger() {
                    @Override
                    public Instant nextExecution(TriggerContext triggerContext) {
                        Instant lastCompletion = triggerContext.lastCompletion();
                        long delay = restPollingConfigService.getPollingDelayMs();

                        if (lastCompletion == null) {
                            return Instant.now().plusMillis(delay);
                        }

                        return lastCompletion.plusMillis(delay);
                    }
                }
        );
    }
}
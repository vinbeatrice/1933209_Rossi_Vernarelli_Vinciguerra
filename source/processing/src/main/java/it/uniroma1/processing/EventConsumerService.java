package it.uniroma1.processing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import it.uniroma1.processing.normalization_helpers.NormalizedEvent;

@Service
public class EventConsumerService {

    private final RuleEvaluationService ruleEvaluationService;

    public EventConsumerService(RuleEvaluationService ruleEvaluationService) {
        this.ruleEvaluationService = ruleEvaluationService;
    }

    @RabbitListener(queues = RabbitMQConfig.PROCESSING_QUEUE)
    public void receiveSensorEvent(NormalizedEvent event) {
        System.out.println("Received event from sensor: " + event.getSourceId()
                + " value=" + event.getMeasurements());

        ruleEvaluationService.evaluate(event);
    }
}
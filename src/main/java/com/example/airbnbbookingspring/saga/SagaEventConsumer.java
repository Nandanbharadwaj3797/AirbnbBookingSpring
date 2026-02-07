package com.example.airbnbbookingspring.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumes saga events from Kafka and processes them using a processor
 * abstraction.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventConsumer {
    private final ObjectMapper objectMapper;
    private final SagaEventProcessor sagaEventProcessor;

    /**
     * Consumes saga events from Kafka topic and processes them.
     */
    @KafkaListener(topics = "${airbnb.saga.kafka.topic:saga-events}", groupId = "saga-group")
    public void onMessage(String eventJson) {
        if (eventJson == null || eventJson.isEmpty()) {
            log.warn("Received empty or null event from Kafka topic");
            return;
        }
        try {
            SagaEvent sagaEvent = objectMapper.readValue(eventJson, SagaEvent.class);
            log.info("Processing saga event: {}", sagaEvent.getSagaId());
            sagaEventProcessor.processEvent(sagaEvent);
            log.info("Saga event processed successfully for saga id: {}", sagaEvent.getSagaId());
        } catch (Exception e) {
            log.error("Error processing saga event: {}", e.getMessage(), e);
            // Optionally: send to DLQ or retry
        }
    }
}
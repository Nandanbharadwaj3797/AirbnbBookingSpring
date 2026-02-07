package com.example.airbnbbookingspring.saga;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
/**
 * Consumes saga events from a queue and processes them using a receiver
 * abstraction.
 */
public class SagaEventConsumer {
    private final SagaEventReceiver sagaEventReceiver;
    private final ObjectMapper objectMapper;
    private final SagaEventProcessor sagaEventProcessor;

    /**
     * Polls the saga queue for events and processes them.
     */
    @Scheduled(fixedDelay = 500)
    public void consumeEvents() {
        try {
            String eventJson = sagaEventReceiver.receive();
            log.info("Event JSON: {}", eventJson);
            if (eventJson != null && !eventJson.isEmpty()) {
                SagaEvent sagaEvent = objectMapper.readValue(eventJson, SagaEvent.class);
                log.info("Processing saga event: {}", sagaEvent.getSagaId());
                sagaEventProcessor.processEvent(sagaEvent);
                log.info("Saga event processed successfully for saga id: {}", sagaEvent.getSagaId());
            }
        } catch (Exception e) {
            log.error("Error processing saga event: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to process saga event", e);
        }
    }
}
package com.example.airbnbbookingspring.saga;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Publishes saga events to a Redis queue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SagaEventPublisher {

    @Value("${airbnb.saga.queue:saga:events}")
    private String sagaQueue;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes a saga event to the configured Redis queue.
     * @param eventType Event type
     * @param step Saga step
     * @param payload Event payload
     */
    public void publishEvent(String eventType, String step, Map<String, Object> payload) {
        SagaEvent sagaEvent = SagaEvent.builder()
                .sagaId(UUID.randomUUID().toString())
                .eventType(eventType)
                .step(step)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .status(SagaEvent.SagaStatus.PENDING.name())
                .build();
        try {
            String eventJson = objectMapper.writeValueAsString(sagaEvent);
            redisTemplate.opsForList().rightPush(sagaQueue, eventJson);
        } catch (Exception e) {
            log.error("Failed to publish saga event: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to publish saga event", e);
        }
    }

}
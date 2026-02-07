package com.example.airbnbbookingspring.saga;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaSagaEventSender implements SagaEventSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    @Override
    public void send(String eventJson) {
        kafkaTemplate.send(topic, eventJson);
    }
}

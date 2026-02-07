package com.example.airbnbbookingspring.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.airbnbbookingspring.saga.KafkaSagaEventSender;
import com.example.airbnbbookingspring.saga.KafkaSagaEventReceiver;

@Configuration
public class SagaKafkaQueueConfig {
    @Value("${airbnb.saga.kafka.topic:saga-events}")
    private String sagaKafkaTopic;

    @Bean
    public KafkaSagaEventSender kafkaSagaEventSender(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaSagaEventSender(kafkaTemplate, sagaKafkaTopic);
    }

    @Bean
    public KafkaSagaEventReceiver kafkaSagaEventReceiver() {
        return new KafkaSagaEventReceiver();
    }
}

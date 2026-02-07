package com.example.airbnbbookingspring.saga;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class KafkaSagaEventReceiver implements SagaEventReceiver {
    private final BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "${airbnb.saga.kafka.topic:saga-events}", groupId = "saga-group")
    public void listen(ConsumerRecord<String, String> record) {
        eventQueue.offer(record.value());
    }

    @Override
    public String receive() {
        return eventQueue.poll();
    }
}

package com.example.airbnbbookingspring.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import com.example.airbnbbookingspring.saga.RedisSagaEventSender;
import com.example.airbnbbookingspring.saga.RedisSagaEventReceiver;

@Configuration
public class SagaQueueConfig {
    @Value("${airbnb.saga.queue:saga:events}")
    private String sagaQueue;

    @Bean
    public RedisSagaEventSender redisSagaEventSender(RedisTemplate<String, String> redisTemplate) {
        return new RedisSagaEventSender(sagaQueue, redisTemplate);
    }

    @Bean
    public RedisSagaEventReceiver redisSagaEventReceiver(RedisTemplate<String, String> redisTemplate) {
        return new RedisSagaEventReceiver(sagaQueue, redisTemplate);
    }
}

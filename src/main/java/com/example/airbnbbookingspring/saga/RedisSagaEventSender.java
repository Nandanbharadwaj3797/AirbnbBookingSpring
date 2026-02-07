package com.example.airbnbbookingspring.saga;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSagaEventSender implements SagaEventSender {
    private final String sagaQueue;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void send(String eventJson) {
        redisTemplate.opsForList().rightPush(sagaQueue, eventJson);
    }
}

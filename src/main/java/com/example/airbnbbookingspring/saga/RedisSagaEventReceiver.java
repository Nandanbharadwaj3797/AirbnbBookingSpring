package com.example.airbnbbookingspring.saga;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisSagaEventReceiver implements SagaEventReceiver {
    private final String sagaQueue;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String receive() {
        return redisTemplate.opsForList().leftPop(sagaQueue, 1, TimeUnit.SECONDS);
    }
}

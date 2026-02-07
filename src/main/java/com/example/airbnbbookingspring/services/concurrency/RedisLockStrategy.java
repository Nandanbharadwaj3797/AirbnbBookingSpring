package com.example.airbnbbookingspring.services.concurrency;



import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.airbnbbookingspring.models.Availability;
import com.example.airbnbbookingspring.repositories.writes.AvailabilityWriteRepository;

import lombok.RequiredArgsConstructor;


/**
 * Implements a Redis-based locking strategy for concurrency control.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisLockStrategy implements ConcurrencyControlStrategy {


    private static final String LOCK_KEY_PREFIX = "lock:availability:";

    @Value("${airbnb.lock.timeout-minutes:2}")
    private long lockTimeoutMinutes;

    private final RedisTemplate<String, String> redisTemplate;
    private final AvailabilityWriteRepository availabilityWriteRepository;


    /**
     * Releases the lock for the given Airbnb and date range.
     */
    @Override
    public void releaseLock(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate) {
        String lockKey = generateLockKey(airbnbId, checkInDate, checkOutDate);
        redisTemplate.delete(lockKey);
    }


    /**
     * Attempts to acquire a lock and check availability for the given Airbnb and date range.
     * @throws IllegalStateException if lock cannot be acquired or dates are unavailable
     */
    @Override
    public List<Availability> lockAndCheckAvailability(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate, Long userId) {
        Long bookedSlots = availabilityWriteRepository.countByAirbnb_IdAndDateBetweenAndBookingIsNotNull(airbnbId, checkInDate, checkOutDate);
        if(bookedSlots > 0) {
            log.error("Airbnb is not available for all the given dates. AirbnbId: {}", airbnbId);
            throw new IllegalStateException("Airbnb is not available for all the given dates. Please try again with different dates.");
        }
        String lockKey = generateLockKey(airbnbId, checkInDate, checkOutDate);
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), Duration.ofMinutes(lockTimeoutMinutes));
        if(!locked) {
            log.error("Failed to acquire booking lock for AirbnbId: {}", airbnbId);
            throw new IllegalStateException("Failed to acquire booking for the given dates. Please try again.");
        }
        try {
            return availabilityWriteRepository.findByAirbnb_IdAndDateBetween(airbnbId, checkInDate, checkOutDate);
        } catch (Exception e) {
            log.error("Error during availability check: {}", e.getMessage(), e);
            releaseLock(airbnbId, checkInDate, checkOutDate);
            throw new IllegalStateException("Error during availability check", e);
        }
    }

    private String generateLockKey(Long airbnbId, LocalDate checkInDate, LocalDate checkOutDate) {
        return LOCK_KEY_PREFIX + airbnbId + ":" + checkInDate + ":" + checkOutDate;
    }
}
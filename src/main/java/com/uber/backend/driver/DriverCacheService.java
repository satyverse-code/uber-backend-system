package com.uber.backend.driver;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
public class DriverCacheService {

    private final RedisTemplate<String, Object> redis;
    private final MeterRegistry meterRegistry;

    public DriverCacheService(RedisTemplate<String, Object> redis, MeterRegistry meterRegistry) {
        this.redis = redis;
        this.meterRegistry = meterRegistry;
    }

    public Optional<Map<String, Object>> getDriverSnapshot(Long driverId) {
        String key = key(driverId);
        Object val = redis.opsForValue().get(key);
        if (val instanceof Map<?, ?> map) {
            meterRegistry.counter("driver_cache_hits_total").increment();
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) map;
            return Optional.of(m);
        }
        meterRegistry.counter("driver_cache_misses_total").increment();
        return Optional.empty();
    }

    public void putDriverSnapshot(Long driverId, String status, Double lat, Double lng) {
        String key = key(driverId);
        Map<String, Object> payload = Map.of(
                "status", status,
                "lat", lat,
                "lng", lng
        );
        redis.opsForValue().set(key, payload, Duration.ofMinutes(10));
    }

    private String key(Long driverId) {
        return "driver:" + driverId;
    }
}

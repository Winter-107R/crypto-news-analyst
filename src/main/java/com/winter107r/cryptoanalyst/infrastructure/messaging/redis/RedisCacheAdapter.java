package com.winter107r.cryptoanalyst.infrastructure.messaging.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winter107r.cryptoanalyst.domain.model.RiskInsight;
import com.winter107r.cryptoanalyst.domain.port.out.CachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/** Redis-backed cache for RiskInsight — TTL 5 minutes. */
@Component
public class RedisCacheAdapter implements CachePort {

    private static final Logger log    = LoggerFactory.getLogger(RedisCacheAdapter.class);
    private static final Duration TTL  = Duration.ofMinutes(5);

    private final StringRedisTemplate redis;
    private final ObjectMapper        mapper;

    public RedisCacheAdapter(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis  = redis;
        this.mapper = mapper;
    }

    @Override
    public Optional<RiskInsight> get(String key) {
        try {
            String json = redis.opsForValue().get(Objects.requireNonNull(key));
            if (json == null) return Optional.empty();
            return Optional.of(mapper.readValue(json, RiskInsight.class));
        } catch (Exception e) {
            log.warn("Cache read failed for key={}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("null")
    public void put(String key, RiskInsight insight) {
        Objects.requireNonNull(key, "cache key must not be null");
        try {
            redis.opsForValue().set(key, mapper.writeValueAsString(insight), TTL);
        } catch (Exception e) {
            log.warn("Cache write failed for key={}: {}", key, e.getMessage());
        }
    }
}

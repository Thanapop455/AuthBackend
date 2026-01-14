package com.example.Backend_java_newbie.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final StringRedisTemplate redis;

    private static final String PREFIX = "bl:jti:";

    public void blacklist(String jti, Instant expiresAt) {
        long ttlSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
        if (ttlSeconds <= 0) ttlSeconds = 1;

        redis.opsForValue().set(PREFIX + jti, "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isBlacklisted(String jti) {
        Boolean exists = redis.hasKey(PREFIX + jti);
        return Boolean.TRUE.equals(exists);
    }
}

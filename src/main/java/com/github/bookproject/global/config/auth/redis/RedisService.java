package com.github.bookproject.global.config.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setBlacklistToken(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(token, "blacklisted" , Duration.ofMillis(expirationMillis));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}

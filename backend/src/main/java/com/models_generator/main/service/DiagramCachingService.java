package com.models_generator.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class DiagramCachingService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DiagramCachingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOrUpdateDiagram(String sessionId, String json) {
        String key = "diagram:" + sessionId;
        redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(30));
    }

    public Optional<String> getLatestDiagram(String sessionId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("diagram:" + sessionId));
    }
}

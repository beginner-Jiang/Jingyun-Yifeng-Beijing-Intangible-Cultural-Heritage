package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.service.FootprintService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootprintServiceImpl implements FootprintService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_FOOTPRINTS = 30;
    private static final String KEY_PREFIX = "footprint:";

    @Override
    public void addFootprint(Long userId, String targetType, Long targetId, String action) {
        String key = KEY_PREFIX + userId;
        String value = targetType + ":" + targetId + ":" + action + ":" + System.currentTimeMillis();
        redisTemplate.opsForList().leftPush(key, value);
        redisTemplate.opsForList().trim(key, 0, MAX_FOOTPRINTS - 1);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    @Override
    public List<Map<String, Object>> getUserFootprints(Long userId, int limit) {
        String key = KEY_PREFIX + userId;
        List<String> items = redisTemplate.opsForList().range(key, 0, limit - 1);
        if (items == null) return List.of();
        return items.stream().map(item -> {
            String[] parts = item.split(":");
            Map<String, Object> map = new HashMap<>();
            map.put("targetType", parts[0]);
            map.put("targetId", Long.parseLong(parts[1]));
            map.put("action", parts[2]);
            map.put("timestamp", Long.parseLong(parts[3]));
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getUserFootprints(Long userId) {
        return getUserFootprints(userId, MAX_FOOTPRINTS);
    }
}
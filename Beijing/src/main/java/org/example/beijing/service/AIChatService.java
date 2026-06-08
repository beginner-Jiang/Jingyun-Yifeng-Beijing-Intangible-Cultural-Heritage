package org.example.beijing.service;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.entity.AIChatMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_HISTORY = 10;
    private static final String KEY_PREFIX = "ai:chat:";

    @PostConstruct
    public void cleanOldKeys() {
        String[] oldModes = {"text", "image", "video"};
        for (String mode : oldModes) {
            String oldKey = "ai:chat:" + mode;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(oldKey))) {
                redisTemplate.delete(oldKey);
                log.info("已删除旧版共用AI聊天记录: {}", oldKey);
            }
        }
    }

    private String buildKey(Long userId, String mode) {
        return KEY_PREFIX + userId + ":" + mode;
    }

    public void saveMessage(Long userId, String mode, AIChatMessage message) {
        if (userId == null) throw new RuntimeException("用户未登录");
        String key = buildKey(userId, mode);
        String json = JSON.toJSONString(message);
        redisTemplate.opsForList().leftPush(key, json);
        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY - 1);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public List<AIChatMessage> getHistory(Long userId, String mode, int limit) {
        if (userId == null) throw new RuntimeException("用户未登录");
        String key = buildKey(userId, mode);
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, limit - 1);
        if (jsonList == null) return List.of();
        return jsonList.stream()
                .map(json -> JSON.parseObject(json, AIChatMessage.class))
                .collect(Collectors.toList());
    }

    public void clearHistory(Long userId, String mode) {
        if (userId == null) return;
        redisTemplate.delete(buildKey(userId, mode));
    }

    public void deleteAllUserHistory(Long userId) {
        if (userId == null) return;
        clearHistory(userId, "text");
        clearHistory(userId, "image");
        clearHistory(userId, "video");
    }
}
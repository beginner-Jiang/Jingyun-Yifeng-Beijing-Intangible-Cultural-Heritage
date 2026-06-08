package org.example.beijing.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.entity.ChatMessage;
import org.example.beijing.service.AIChatHistoryService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatHistoryServiceImpl implements AIChatHistoryService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ai:chat:";
    private static final long EXPIRE_DAYS = 30;

    @Override
    public void saveMessage(Long userId, String mode, ChatMessage message) {
        String key = KEY_PREFIX + userId + ":" + mode;
        String json = JSON.toJSONString(message);
        redisTemplate.opsForList().leftPush(key, json);
        redisTemplate.expire(key, EXPIRE_DAYS, TimeUnit.DAYS);
    }

    @Override
    public List<ChatMessage> getHistory(Long userId, String mode) {
        String key = KEY_PREFIX + userId + ":" + mode;
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (jsonList == null || jsonList.isEmpty()) {
            return new ArrayList<>();
        }
        List<ChatMessage> messages = new ArrayList<>();
        for (String json : jsonList) {
            try {
                messages.add(JSON.parseObject(json, ChatMessage.class));
            } catch (Exception e) {
                log.error("解析消息失败，json: {}", json, e);
            }
        }
        return messages;
    }

    @Override
    public void clearHistory(Long userId, String mode) {
        String key = KEY_PREFIX + userId + ":" + mode;
        redisTemplate.delete(key);
    }
}
package org.example.beijing.service;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;
    private static final long CACHE_EXPIRE_MINUTES = 30;

    public <T> void setList(String key, List<T> list) {
        String json = JSON.toJSONString(list);
        redisTemplate.opsForValue().set(key, json, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseArray(json, clazz);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public String buildUserActivitiesKey(Long userId) {
        return "user:activities:" + userId;
    }

    public String buildUserApplicationsKey(Long userId) {
        return "user:applications:" + userId;
    }

    public String buildUserPostsKey(Long userId) {
        return "user:posts:" + userId;
    }
}
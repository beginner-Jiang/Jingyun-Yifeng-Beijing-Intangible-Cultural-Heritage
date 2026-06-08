package org.example.beijing.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserService {

    private final Map<Long, Long> userLastActive = new ConcurrentHashMap<>();
    private static final long TIMEOUT_MS = 30 * 60 * 1000; // 30分钟

    /**
     * 更新用户最后活跃时间
     */
    public void updateOnline(Long userId) {
        if (userId != null) {
            userLastActive.put(userId, System.currentTimeMillis());
        }
    }

    /**
     * 判断用户是否在线（活跃时间未超时）
     */
    public boolean isOnline(Long userId) {
        if (userId == null) return false;
        Long last = userLastActive.get(userId);
        if (last == null) return false;
        return (System.currentTimeMillis() - last) <= TIMEOUT_MS;
    }

    /**
     * 获取当前在线用户数
     */
    public int getOnlineCount() {
        cleanExpired();
        return userLastActive.size();
    }

    /**
     * 清理超时用户
     */
    private void cleanExpired() {
        long now = System.currentTimeMillis();
        userLastActive.entrySet().removeIf(entry -> now - entry.getValue() > TIMEOUT_MS);
    }
}
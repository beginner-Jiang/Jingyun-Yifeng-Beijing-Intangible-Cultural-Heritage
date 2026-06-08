package org.example.beijing.service;

import org.example.beijing.entity.ChatMessage;
import java.util.List;

public interface AIChatHistoryService {
    /**
     * 保存用户的一条对话消息
     */
    void saveMessage(Long userId, String mode, ChatMessage message);

    /**
     * 获取指定模式的历史对话（按时间倒序，最新的在前）
     */
    List<ChatMessage> getHistory(Long userId, String mode);

    /**
     * 清空指定模式的对话历史
     */
    void clearHistory(Long userId, String mode);
}
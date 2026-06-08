package org.example.beijing.service;

import org.example.beijing.entity.ForumMessage;
import java.time.LocalDateTime;
import java.util.List;

public interface ForumService {
    List<ForumMessage> getMessages(int limit);
    void postMessage(Long userId, String userName, String content);
    void deleteUserMessages(Long userId);
    List<ForumMessage> getMessagesByTimeRange(LocalDateTime start, LocalDateTime end, int limit);
}
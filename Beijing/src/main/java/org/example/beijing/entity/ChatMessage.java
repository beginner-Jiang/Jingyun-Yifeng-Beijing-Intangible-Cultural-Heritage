package org.example.beijing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * AI对话消息实体，用于存储单条消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String role;          // 'user' 或 'assistant'
    private String type;           // 'text', 'image', 'video'
    private String content;        // 文本内容或媒体URL
    private LocalDateTime timestamp;
}
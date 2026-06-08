package org.example.beijing.dto;

import lombok.Data;
import org.example.beijing.entity.ChatMessage;

@Data
public class ChatHistoryRequest {
    private String mode;           // 'text', 'image', 'video'
    private ChatMessage message;
}
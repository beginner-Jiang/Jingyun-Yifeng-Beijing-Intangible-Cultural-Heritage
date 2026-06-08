package org.example.beijing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIChatMessage {
    private String role;       // "user" 或 "assistant"
    private String type;        // "text", "image", "video"
    private String content;     // 文本内容或媒体URL
    private String timestamp;   // ISO 格式字符串，如 "2026-03-03T12:34:56Z"
}
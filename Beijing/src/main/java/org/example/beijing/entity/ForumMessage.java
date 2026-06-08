package org.example.beijing.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumMessage {
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
}
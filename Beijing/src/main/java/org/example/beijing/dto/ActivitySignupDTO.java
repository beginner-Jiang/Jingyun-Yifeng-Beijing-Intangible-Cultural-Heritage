package org.example.beijing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivitySignupDTO {
    private Long id;
    private Long activityId;
    private Long userId;
    private String username;
    private String status; // pending, accepted, rejected
    private LocalDateTime applyTime;
    private LocalDateTime processTime;
}
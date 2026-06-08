package org.example.beijing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityUpdateDTO {
    private String title;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private String description;
    private String imageUrl;
    private Long siteId;
}
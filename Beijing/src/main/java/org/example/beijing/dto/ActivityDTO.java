package org.example.beijing.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动相关 DTO
 */
public class ActivityDTO {

    /**
     * 发布活动请求 DTO
     */
    @Data
    public static class PublishDTO {
        private String title;               // 活动标题
        private String type;                 // 活动类型
        private Long inheritorId;            // 传承人ID
        private Long siteId;                 // 点位ID
        private LocalDateTime startTime;     // 开始时间
        private LocalDateTime endTime;       // 结束时间
        private Integer maxParticipants;     // 最大参与人数
        private String description;          // 活动描述
        private String imageUrl;             // 活动封面图
    }
}
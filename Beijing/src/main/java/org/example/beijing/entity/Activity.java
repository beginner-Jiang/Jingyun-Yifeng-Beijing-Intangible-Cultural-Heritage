package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动表（工坊体验、讲座、展览等）
 */
@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;           // 活动标题
    private String type;            // 活动类型：体验课、讲座、展览等
    private Long inheritorId;       // 主办传承人ID
    private Long siteId;            // 举办点位ID
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private Integer maxParticipants; // 最大参与人数
    private Integer currentParticipants; // 当前报名人数
    private String description;      // 活动描述
    private String imageUrl;         // 活动封面图
    private Integer status;          // 状态：0-待审核，1-已发布，2-已结束

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户足迹表（浏览历史）
 */
@Data
@TableName("footprint")
public class Footprint {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;            // 用户ID
    private String targetType;      // 浏览类型：item、site、activity
    private Long targetId;          // 对应ID
    private String action;          // 动作：view（浏览）、collect（收藏）、signup（报名）等

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
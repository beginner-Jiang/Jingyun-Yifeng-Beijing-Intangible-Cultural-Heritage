package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recruit_post")
public class RecruitPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inheritorId;
    private String title;
    private String description;
    private String inheritorName;
    private Integer applicantCount;
    private Integer maxApplicants; // 新增字段
    private Integer status; // 0-招募中，1-已结束
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
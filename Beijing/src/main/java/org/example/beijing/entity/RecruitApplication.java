package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recruit_application")
public class RecruitApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recruitId;
    private Long userId;
    private String status; // pending, accepted, rejected
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime applyTime;
    private LocalDateTime processTime;
}
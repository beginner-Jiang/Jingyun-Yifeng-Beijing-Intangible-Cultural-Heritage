package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 传承人档案表
 */
@Data
@TableName("inheritor")
public class Inheritor {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 传承人姓名
    private String level;           // 级别（国家级/市级）
    private String gender;          // 性别
    private Integer birthYear;      // 出生年份
    private String biography;       // 人物简介/口述史
    private String imageUrl;        // 照片
    private Long userId;            // 关联的用户ID（如果该传承人也是平台用户）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
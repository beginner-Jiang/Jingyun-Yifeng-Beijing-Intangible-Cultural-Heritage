package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 非遗项目表
 */
@Data
@TableName("heritage_item")
public class HeritageItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 项目名称
    private String category;        // 类别（传统技艺、曲艺等）
    private String region;          // 申报地区
    private String level;           // 级别（国家级/市级）
    private String number;          // 遗产编号
    private String description;     // 详细介绍
    private String imageUrl;        // 图片URL
    private String videoUrl;        // 视频URL
    private Long inheritorId;       // 关联的传承人ID（可空）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
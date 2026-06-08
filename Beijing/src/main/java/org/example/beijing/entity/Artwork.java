package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@TableName("artwork")
public class Artwork {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String imageUrl;
    private Long inheritorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
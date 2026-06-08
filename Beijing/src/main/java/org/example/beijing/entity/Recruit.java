package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recruit")
public class Recruit {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;               // 招募标题
    private String description;          // 招募描述
    private Long inheritorId;            // 发布招募的传承人ID
    private String inheritorName;        // 传承人姓名
    private Integer maxApplicants;       // 最大申请人数（可选）
    private Integer currentApplicants;   // 当前申请人数（待同意+已参与）
    private Integer status;              // 0-进行中，1-已结束
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
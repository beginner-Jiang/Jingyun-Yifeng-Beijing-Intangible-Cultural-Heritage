package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.INPUT)   // 改为手动输入ID
    private Long id;

    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer age;
    private String avatar;
    private String role;
    private String interestTags;
    private String inviteCode;
    private String bio;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
package org.example.beijing.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDTO {
    private String username;      // 昵称
    private Integer age;           // 年龄（新增）
    private String bio;            // 个人简介
    private String avatar;         // 头像URL
    private String interestTags;   // 感兴趣的非遗类别，逗号分隔
}
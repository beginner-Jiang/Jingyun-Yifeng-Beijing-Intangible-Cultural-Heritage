package org.example.beijing.dto;

import lombok.Data;

/**
 * 更新用户信息DTO
 */
@Data
public class UserInfoDTO {
    private String username;   // 昵称
    private String bio;        // 个人简介
    private String avatar;     // 头像URL
    private String interestTags; // 感兴趣的非遗类别，逗号分隔
}
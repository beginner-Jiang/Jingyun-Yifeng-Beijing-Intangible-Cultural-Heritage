package org.example.beijing.dto;

import lombok.Data;

/**
 * 修改密码 DTO
 */
@Data
public class ChangePasswordDTO {
    private String oldPassword;   // 旧密码
    private String newPassword;   // 新密码
}
package org.example.beijing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    private String account;   // 用户名/手机号/邮箱（但密码登录只接受用户名）

    @NotBlank(message = "密码不能为空")
    private String password;
}
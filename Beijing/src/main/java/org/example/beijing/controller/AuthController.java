package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.LoginDTO;
import org.example.beijing.dto.RegisterDTO;
import org.example.beijing.service.UserService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 密码登录
     */
    @PostMapping("/login")
    public ResponseResult<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return ResponseResult.success(token);
    }

    /**
     * 手机号登录（前端验证码校验后调用）
     */
    @PostMapping("/login-by-phone")
    public ResponseResult<String> loginByPhone(@RequestParam String phone) {
        String token = userService.loginByPhone(phone);
        return ResponseResult.success(token);
    }

    /**
     * 注册（包含年龄字段）
     */
    @PostMapping("/register")
    public ResponseResult<?> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseResult.success("注册成功");
    }

    /**
     * 忘记密码（发送重置链接）
     */
    @PostMapping("/forgot-password")
    public ResponseResult<?> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseResult.success("重置链接已发送至邮箱");
    }
}
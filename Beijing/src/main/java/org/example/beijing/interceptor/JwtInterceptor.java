package org.example.beijing.interceptor;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.util.JwtUtil;
import org.example.beijing.util.ResponseResult;
import org.example.beijing.service.OnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OnlineUserService onlineUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "未提供有效的认证令牌");
            return false;
        }

        token = token.substring(7);

        if (!jwtUtil.validateToken(token)) {
            sendUnauthorized(response, "认证令牌无效或已过期");
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                sendUnauthorized(response, "令牌缺少用户标识");
                return false;
            }
            Long userId;
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else {
                userId = Long.parseLong(userIdObj.toString());
            }
            String role = claims.get("role", String.class);
            if (role == null) role = "user";
            request.setAttribute("userId", userId);
            request.setAttribute("userRole", role);

            // 更新在线状态
            onlineUserService.updateOnline(userId);

            return true;
        } catch (Exception e) {
            log.error("解析 token 失败", e);
            sendUnauthorized(response, "认证令牌解析失败");
            return false;
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(ResponseResult.error(401, message)));
        writer.flush();
        writer.close();
    }
}
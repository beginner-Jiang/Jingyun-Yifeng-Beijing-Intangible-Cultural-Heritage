package org.example.beijing.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 JWT Token（三参数版本，包含 userId、username、role）
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return token字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        if (username != null) {
            claims.put("username", username);
        }
        claims.put("role", role);
        // 重要：设置 subject 为 userId 的字符串形式，避免 null
        return generateToken(claims, String.valueOf(userId));
    }

    /**
     * 生成 JWT Token（两参数版本，兼容旧代码）
     */
    public String generateToken(Long userId, String role) {
        return generateToken(userId, null, role);
    }

    /**
     * 内部方法，基于 claims 和 subject 生成 token
     */
    private String generateToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)   // 关键修复：设置 subject 不为 null
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 JWT Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID（优先取 claims 中的 userId，兼容取 subject）
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj != null) {
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            }
            return Long.parseLong(userIdObj.toString());
        }
        // 兼容旧 token：从 subject 中解析
        String subject = claims.getSubject();
        if (subject != null && !subject.isEmpty()) {
            return Long.parseLong(subject);
        }
        return null;
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从 Token 中获取用户角色
     */
    public String getUserRole(String token) {
        return parseToken(token).get("role", String.class);
    }
}
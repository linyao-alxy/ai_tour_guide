package com.guide.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT令牌工具 — 双密钥管理：C端和B端使用不同密钥
 */
@Component
public class JwtTokenProvider {

    private final SecretKey touristKey;
    private final SecretKey adminKey;

    @Value("${jwt.tourist-expiration:604800000}")  // 7天
    private long touristExpiration;

    @Value("${jwt.admin-expiration:86400000}")      // 24小时
    private long adminExpiration;

    public JwtTokenProvider(
            @Value("${jwt.tourist-secret:TouristSecretKeyForAI-Digital-Guide-2026}") String touristSecret,
            @Value("${jwt.admin-secret:AdminSecretKeyForAI-Digital-Guide-2026!!}") String adminSecret) {
        this.touristKey = Keys.hmacShaKeyFor(touristSecret.getBytes(StandardCharsets.UTF_8));
        this.adminKey = Keys.hmacShaKeyFor(adminSecret.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成C端令牌 */
    public String generateTouristToken(Long userId, Map<String, Object> claims) {
        return buildToken(userId, claims, touristExpiration, touristKey);
    }

    /** 生成B端令牌 */
    public String generateAdminToken(Long adminId, Map<String, Object> claims) {
        return buildToken(adminId, claims, adminExpiration, adminKey);
    }

    /** 验证C端令牌 */
    public Claims validateTouristToken(String token) {
        return validateToken(token, touristKey);
    }

    /** 验证B端令牌 */
    public Claims validateAdminToken(String token) {
        return validateToken(token, adminKey);
    }

    private String buildToken(Long userId, Map<String, Object> claims, long expiration, SecretKey key) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    private Claims validateToken(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

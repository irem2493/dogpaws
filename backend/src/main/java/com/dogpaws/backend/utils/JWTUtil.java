package com.dogpaws.backend.utils;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
@Slf4j
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        System.out.println("secretKey : " + secretKey.toString() + ", algorithm : " + secretKey.getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getNickname(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }

    public Boolean isExpired(String token) {
        try {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("⏳ 토큰 만료됨: {}", token);
            return true; // ✅ 만료된 경우 true 반환
        } catch (Exception e) {
            log.error("❌ 토큰 검증 실패: {}", e.getMessage());
            return false; // ✅ 예외 발생 시 안전하게 만료된 것으로 처리
        }
    }

    public String createJwt(String username, String role, String nickname, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // Access Token 생성 (10분 유효)
    public String generateAccessToken(String username, String role, String nickname ) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() +60 * 60*1000))// 10분 유효 - > 1시간 유효하도록 변경
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성 (12시간 유효)
    public String generateRefreshToken(String username, String role, String nickname) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 *1000))// 12시간 유효
                .signWith(secretKey)
                .compact();
    }
}

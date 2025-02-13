package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.utils.JWTUtil;
import com.dogpaws.backend.service.ajy.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class TokenCheck {
    private final JWTUtil jwtUtil;  
    private final TokenService tokenService;


    @PostMapping("/verify-token")
    public ApiResponse<?> verifyToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            String nickname = jwtUtil.getNickname(token);

            TokenUserDto userDto = new TokenUserDto();
            userDto.setUsername(username);
            userDto.setRole(role);
            userDto.setNickname(nickname);

            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, userDto);
            }catch (ExpiredJwtException e) {
                log.warn("Access Token이 만료되었습니다.");
                return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, 401);
            }
        }
        log.error("Authorization 헤더가 없거나 올바르지 않습니다.");
        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
    }

    //이 부분 수정
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("🔄 Refresh Token 요청 받음");

        try {
            // ✅ Refresh Token이 만료되었는지 확인
            if (jwtUtil.isExpired(refreshToken)) {
                log.warn("❌ Refresh Token이 만료되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "ERROR", "message", "Expired Refresh Token"));
            }

            // ✅ Refresh Token에서 사용자 정보 추출
            String username = jwtUtil.getUsername(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String nickname = jwtUtil.getNickname(refreshToken);

            // ✅ Refresh Token 검증 (DB에서 유효한지 확인)
            if (!tokenService.validateRefreshToken(username, refreshToken)) {
                log.warn("❌ 유효하지 않은 Refresh Token: " + refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "ERROR", "message", "Invalid Refresh Token"));
            }

            // ✅ 새로운 Access Token 생성
            String newAccessToken = jwtUtil.generateAccessToken(username, role, nickname);

            log.info("✅ 새로운 Access Token 발급 완료: " + newAccessToken);

            // ✅ 프론트엔드가 받을 수 있도록 JSON 응답
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "accessToken", newAccessToken
            ));

        } catch (Exception e) {
            log.error("❌ Refresh Token 처리 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", "Token Processing Error"));
        }
    }




    //로그아웃
    @PostMapping("/auth/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        String username = jwtUtil.getUsername(refreshToken);

        System.out.println("logout username : " + username);

        if (refreshToken != null) {
            tokenService.deleteRefreshToken(username);
            log.info("로그아웃 성공: 리프레쉬 토큰 삭제 완료");
        }

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "로그아웃 성공");
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh-Token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/auth/token/refresh")
    public ApiResponse<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                String username = jwtUtil.getUsername(refreshToken);
                String role = jwtUtil.getRole(refreshToken);
                String nickname = jwtUtil.getNickname(refreshToken);

                // 새로운 Access Token 생성
                String newAccessToken = jwtUtil.generateAccessToken(username, role, nickname);

                System.out.println("newAccessToken : " + newAccessToken);

                Map<String, String> response = Map.of("accessToken", newAccessToken);
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, response);
            } else {
                return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
            }
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생: {}", e.getMessage());
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, null);
        }
    }

    //로그아웃
    @PostMapping("/auth/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            tokenService.deleteRefreshToken(refreshToken);
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

package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.rim.AdminAuthTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * TODO : DB에 저장된 RefreshToken과 요청시 들어온 RefreshToken을 비교해서 유효성 검증 부분 추가하기
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthTokenService adminAuthTokenService;

    /**
     * 관리자 토큰 검증 및 재발급
     * - refreshToken 쿠키가 있는 경우: AccessToken 재발급
     */
    @PostMapping("/token/verify")
    public ResponseEntity<ApiResponse<?>> verifyAndRefreshToken(HttpServletRequest request, @CookieValue(name = "refreshToken", required = false)String refreshToken) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("관리자 토큰 검증/재발급 확인");
        try{
            if(authorization != null && authorization.startsWith("Bearer ")) {
                String accessToken = authorization.substring(7);
                TokenUserDto adminInfo = adminAuthTokenService.extractTokenUserInfo(accessToken);

                if(refreshToken != null) {
                    try {
                        String newAccessToken = adminAuthTokenService.reissueAccessToken(refreshToken);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                                .body(new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, Map.of("message", "AccessToken 재발급 성공", "adminInfo", adminInfo)));
                    } catch (SecurityException reissueException) {
                        log.error("RefreshToken을 재발급 실패: {}", reissueException.getMessage());
                    }
                }
            }
        }catch (SecurityException e){
            log.error("AccessToken 검증증 실패 : {}", e.getMessage());
        }
        // 로그아웃 처리: RefreshToken 쿠키 삭제
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .header(HttpHeaders.SET_COOKIE, "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure")
            .body(new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "유효한 토큰이 없습니다. 로그아웃 처리되었습니다."));
    }

    /**
     * 관리자 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@CookieValue(name = "refreshToken", required = false)String refreshToken) {
        if(refreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "로그아웃 실패: 현재 유효한 토큰이 없습니다."));
        }
        try {
            TokenUserDto userInfo = adminAuthTokenService.extractTokenUserInfo(refreshToken);
            adminAuthTokenService.deleteRefreshToken(userInfo.getUsername());

            log.info("관리자 로그아웃 성공: {}", userInfo.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure")
                    .body(new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "로그아웃 성공"));

        } catch (SecurityException e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "로그아웃 실패: " + e.getMessage()));
        }
    }
}

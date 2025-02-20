package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.service.rim.AdminAuthTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import lombok.extern.slf4j.Slf4j;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
class AdminAuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminAuthTokenService adminAuthTokenService;

    @InjectMocks
    private AdminAuthController adminAuthController;

    @BeforeEach
    void setUp() {
        log.info("=== AdminAuthController 테스트 초기화 시작 ===");
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminAuthController).build();
        log.info("MockMvc 설정 완료");
    }

    @Test
    void testVerifyAndRefreshToken_WithAccessToken() throws Exception {
        log.info("=== AccessToken 검증 테스트 시작 ===");
        
        // given
        String accessToken = "valid.access.token";
        TokenUserDto adminInfo = new TokenUserDto("admin", "ROLE_ADMIN", "AdminUser");
        log.info("테스트 데이터 설정 - AccessToken: {}, AdminInfo: {}", accessToken, adminInfo);

        when(adminAuthTokenService.extractTokenUserInfo(accessToken)).thenReturn(adminInfo);
        log.info("Mock 설정 완료 - adminAuthTokenService.extractTokenUserInfo()");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/token/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.body.adminInfo.username").value("admin"))
                .andReturn();

        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("API 응답 검증 완료");
        log.info("=== AccessToken 검증 테스트 완료 ===");
    }

    @Test
    void testVerifyAndRefreshToken_WithRefreshToken() throws Exception {
        log.info("=== RefreshToken을 통한 AccessToken 재발급 테스트 시작 ===");
        
        // given
        String refreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";
        log.info("테스트 데이터 설정 - RefreshToken: {}, 새로운 AccessToken: {}", refreshToken, newAccessToken);

        when(adminAuthTokenService.reissueAccessToken(refreshToken)).thenReturn(newAccessToken);
        log.info("Mock 설정 완료 - adminAuthTokenService.reissueAccessToken()");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/token/verify")
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andReturn();
                
        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("API 응답 헤더 - Authorization: {}", result.getResponse().getHeader(HttpHeaders.AUTHORIZATION));
        log.info("API 응답 검증 완료");
        log.info("=== RefreshToken을 통한 AccessToken 재발급 테스트 완료 ===");
    }

    @Test
    void testLogout_Success() throws Exception {
        log.info("=== 로그아웃 성공 테스트 시작 ===");

        // given
        String accessToken = "valid.access.token";
        String refreshToken = "valid.refresh.token";
        TokenUserDto adminInfo = new TokenUserDto("admin", "관리자", "ROLE_ADMIN");
        log.info("테스트 데이터 설정 - AccessToken: {}, RefreshToken: {}, AdminInfo: {}", accessToken, refreshToken, adminInfo);

        when(adminAuthTokenService.extractTokenUserInfo(refreshToken)).thenReturn(adminInfo);
        log.info("Mock 설정 완료 - adminAuthTokenService.extractTokenUserInfo()");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(cookie().value("refreshToken", ""))
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION))
                .andReturn();

        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("API 응답 검증 완료");
        log.info("=== 로그아웃 성공 테스트 완료 ===");
    }

    @Test
    void testVerifyAndRefreshToken_WithExpiredAccessToken() throws Exception {
        log.info("=== 만료된 AccessToken 검증 테스트 시작 ===");
        
        // given
        String expiredAccessToken = "expired.access.token";
        String refreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";
        log.info("테스트 데이터 설정 - 만료된 AccessToken: {}, RefreshToken: {}, 새로운 AccessToken: {}", 
            expiredAccessToken, refreshToken, newAccessToken);

        // 만료된 토큰으로 인한 예외 시뮬레이션
        when(adminAuthTokenService.extractTokenUserInfo(expiredAccessToken))
            .thenThrow(new IllegalArgumentException("만료된 토큰입니다."));
        log.info("만료된 토큰 예외 시뮬레이션 설정 완료");

        // RefreshToken을 통한 새로운 AccessToken 발급 시뮬레이션
        when(adminAuthTokenService.reissueAccessToken(refreshToken))
            .thenReturn(newAccessToken);
        log.info("새로운 AccessToken 발급 Mock 설정 완료");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/token/verify")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccessToken)
                .cookie(new Cookie("refreshToken", refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.body.tokenReissued").value(true))
                .andReturn();

        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("새로 발급된 AccessToken 검증: {}", result.getResponse().getHeader(HttpHeaders.AUTHORIZATION));
        
        // 토큰 재발급 서비스 호출 검증
        verify(adminAuthTokenService).reissueAccessToken(refreshToken);
        log.info("토큰 재발급 서비스 호출 검증 완료");
        
        log.info("=== 만료된 AccessToken 검증 테스트 완료 ===");
    }

    @Test
    void testVerifyAndRefreshToken_WithInvalidAccessTokenAndNoRefreshToken() throws Exception {
        log.info("=== 유효하지 않은 AccessToken 및 RefreshToken 부재 테스트 시작 ===");

        // given
        String invalidAccessToken = "invalid.access.token";
        log.info("테스트 데이터 설정 - 유효하지 않은 AccessToken: {}", invalidAccessToken);

        when(adminAuthTokenService.extractTokenUserInfo(invalidAccessToken))
                .thenThrow(new SecurityException("유효하지 않은 토큰입니다."));
        log.info("유효하지 않은 토큰 예외 시뮬레이션 설정 완료");

        // when & then
        log.info("API 요청 실행 - 401 Unauthorized 응답 예상");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/token/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidAccessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("유효한 토큰이 없습니다. 로그아웃이 필요합니다."))
                .andReturn();

        // 응답 상태 코드 상세 검증
        int statusCode = result.getResponse().getStatus();
        log.info("응답 상태 코드: {}", statusCode);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, statusCode, "상태 코드는 401이어야 합니다.");

        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("401 Unauthorized 상태 코드 검증 완료");

        // 인증 헤더 부재 검증
        assertNull(result.getResponse().getHeader(HttpHeaders.AUTHORIZATION),
                "인증 실패 시 Authorization 헤더가 없어야 합니다.");
        log.info("인증 헤더 부재 확인 완료");

        log.info("=== 유효하지 않은 AccessToken 및 RefreshToken 부재 테스트 완료 ===");
    }

    @Test
    void testVerifyAndRefreshToken_WithInvalidAccessTokenAndValidRefreshToken() throws Exception {
        log.info("=== 유효하지 않은 AccessToken과 유효한 RefreshToken 테스트 시작 ===");

        // given
        String invalidAccessToken = "invalid.access.token";
        String validRefreshToken = "valid.refresh.token";
        String newAccessToken = "new.access.token";
        log.info("테스트 데이터 설정 - 유효하지 않은 AccessToken: {}, 유효한 RefreshToken: {}, 새로운 AccessToken: {}",
                invalidAccessToken, validRefreshToken, newAccessToken);

        // 유효하지 않은 AccessToken으로 인한 예외 시뮬레이션
        when(adminAuthTokenService.extractTokenUserInfo(invalidAccessToken))
                .thenThrow(new SecurityException("유효하지 않은 토큰입니다."));

        // RefreshToken을 통한 새로운 AccessToken 발급 시뮬레이션
        when(adminAuthTokenService.reissueAccessToken(validRefreshToken))
                .thenReturn(newAccessToken);
        log.info("Mock 설정 완료");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/token/verify")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidAccessToken)
                        .cookie(new Cookie("refreshToken", validRefreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.body").value("토큰 재발급 성공"))
                .andReturn();

        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("새로 발급된 AccessToken 검증: {}", result.getResponse().getHeader(HttpHeaders.AUTHORIZATION));

        // 토큰 재발급 서비스 호출 검증
        verify(adminAuthTokenService).reissueAccessToken(validRefreshToken);
        log.info("토큰 재발급 서비스 호출 검증 완료");

        log.info("=== 유효하지 않은 AccessToken과 유효한 RefreshToken 테스트 완료 ===");
    }
}
package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.service.rim.AdminAuthTokenService;
import jakarta.servlet.http.Cookie;
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
        String refreshToken = "valid.refresh.token";
        TokenUserDto adminInfo = new TokenUserDto("admin", "ROLE_ADMIN", "AdminUser");
        log.info("테스트 데이터 설정 - RefreshToken: {}, AdminInfo: {}", refreshToken, adminInfo);

        when(adminAuthTokenService.extractTokenUserInfo(refreshToken)).thenReturn(adminInfo);
        log.info("Mock 설정 완료 - adminAuthTokenService.extractTokenUserInfo()");

        // when & then
        log.info("API 요청 실행");
        MvcResult result = mockMvc.perform(post("/api/admin/auth/logout")
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andReturn();
                
        log.info("API 응답 데이터: {}", result.getResponse().getContentAsString());
        log.info("API 응답 검증 완료");
        log.info("=== 로그아웃 성공 테스트 완료 ===");
    }
}
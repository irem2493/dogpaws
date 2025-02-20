package com.dogpaws.backend.controller.rim;

import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.filter.TestableAdminLoginFilter;
import com.dogpaws.backend.repository.jpa.ajy.UserRepository;
import com.dogpaws.backend.service.ajy.TokenService;
import com.dogpaws.backend.service.common.CustomUserDetails;
import com.dogpaws.backend.utils.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AdminLoginFilterTest {

    private TestableAdminLoginFilter adminLoginFilter;
    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;
    private TokenService tokenService;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp(){
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JWTUtil.class);
        tokenService = mock(TokenService.class);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        adminLoginFilter = new TestableAdminLoginFilter(authenticationManager, jwtUtil, tokenService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

    }

    @Test
    @DisplayName("관리자 로그인 성공 테스트")
    void attemptAuthentication_Success() throws Exception{
        log.info("=== 관리자 로그인 성공 테스트 시작 ===");
        //given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        String encodedPw = bCryptPasswordEncoder.encode("admin");
        loginRequest.put("password", encodedPw);
        log.info("테스트 데이터 설정 - username: admin, 암호화된 비밀번호 생성 완료");

        String requestBody = objectMapper.writeValueAsString(loginRequest);
        request.setContent(requestBody.getBytes());
        log.info("요청 본문 설정 완료: {}", requestBody);

        CustomUserDetails adminDetails = new CustomUserDetails(
                User.builder()
                        .username("admin")
                        .password(encodedPw)
                        .role("ROLE_ADMIN")
                        .nickname("관리자")
                .build()
        );

        log.info("관리자 상세 정보 생성 완료");
        log.info("사용자 권한 정보: {}", adminDetails.getAuthorities());

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                adminDetails,
                null,
                adminDetails.getAuthorities()
        );
        log.info("인증 객체 생성 완료");

        when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);
        log.info("인증 매니저 Mock 설정 완료");

        //when
        log.info("인증 시도 시작");
        Authentication result = adminLoginFilter.attemptAuthentication(request, response);
        log.info("인증 시도 완료");

        //then
        log.info("=== 검증 시작 ===");
        assertNotNull(result, "인증 결과가 null이 아니어야 함");
        log.info("인증 결과 null 체크 통과");

        assertEquals("admin", result.getName(), "사용자명이 일치해야 함");
        log.info("사용자명 일치 검증 통과");

        assertTrue(result.getAuthorities().iterator().next().getAuthority().equals("ROLE_ADMIN"), "관리자 권한을 가지고 있어야 함");
        log.info("권한 검증 통과");
        log.info("=== 관리자 로그인 성공 테스트 완료 ===");
    }

    @Test
    @DisplayName("관리자 로그인 실패 - 잘못된 요청 형식")
    void attemptAuthentication_Fail_InvalidRequest() throws Exception {
        log.info("=== 잘못된 요청 형식 테스트 시작 ===");
        // given
        String invalidJson = "잘못된 JSON 형식";
        request.setContent(invalidJson.getBytes());
        log.info("잘못된 JSON 요청 데이터 설정: {}", invalidJson);

        // when & then
        log.info("예외 발생 테스트 시작");
        Exception e = assertThrows(RuntimeException.class, () -> {
            adminLoginFilter.attemptAuthentication(request, response);
        }, "잘못된 JSON 형식에 대해 RuntimeException이 발생해야 함");

        log.info("발생한 예외 타입: {}", e.getClass().getName());
        log.info("예외 메시지: {}", e.getMessage());
        log.info("=== 잘못된 요청 형식 테스트 완료 ===");
    }

    @Test
    @DisplayName("로그인 성공 후 토큰 생성 검증")
    void successfulAuthentication_TokenGeneration() throws Exception {
        log.info("=== 토큰 생성 검증 테스트 시작 ===");
        // given
        CustomUserDetails adminDetails = new CustomUserDetails(
                User.builder()
                        .username("admin")
                        .password("admin")
                        .role("ROLE_ADMIN")
                        .nickname("관리자")
                        .build()
        );
        log.info("테스트용 관리자 상세 정보 생성 완료");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                adminDetails,
                null,
                adminDetails.getAuthorities()
        );
        log.info("인증 객체 생성 완료");

        when(jwtUtil.generateAccessToken(anyString(), anyString(), anyString()))
                .thenReturn("mock.access.token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString(), anyString()))
                .thenReturn("mock.refresh.token");
        log.info("토큰 생성 Mock 설정 완료");

        // when
        log.info("인증 성공 처리 시작");
        adminLoginFilter.successfulAuthentication(request, response, null, authentication);
        log.info("인증 성공 처리 완료");

        // then
        log.info("=== 검증 시작 ===");
        
        // Authorization 헤더 검증
        String authHeader = response.getHeader("Authorization");
        log.info("Authorization 헤더 검증 - 실제 값: {}", authHeader);
        assertEquals("Bearer mock.access.token", authHeader, "Authorization 헤더 값이 예상과 일치해야 함");
        log.info("Authorization 헤더 검증 통과");

        // RefreshToken 쿠키 검증
        Cookie refreshTokenCookie = response.getCookie("refreshToken");
        log.info("RefreshToken 쿠키 검증 시작");
        assertNotNull(refreshTokenCookie, "RefreshToken 쿠키가 존재해야 함");
        assertEquals("mock.refresh.token", refreshTokenCookie.getValue(), "RefreshToken 값이 일치해야 함");
        assertTrue(refreshTokenCookie.isHttpOnly(), "쿠키는 HttpOnly여야 함");
        assertTrue(refreshTokenCookie.getSecure(), "쿠키는 Secure여야 함");
        assertEquals("/", refreshTokenCookie.getPath(), "쿠키 경로가 루트여야 함");
        log.info("RefreshToken 쿠키 검증 통과 - HttpOnly: {}, Secure: {}, Path: {}", 
            refreshTokenCookie.isHttpOnly(), refreshTokenCookie.getSecure(), refreshTokenCookie.getPath());

        // 토큰 저장 검증
        verify(tokenService).saveRefreshToken("admin", "mock.refresh.token");
        log.info("토큰 저장 서비스 호출 검증 통과");

        // 응답 형식 검증
        String contentType = response.getContentType();
        log.info("응답 Content-Type 검증: {}", contentType);
        assertEquals(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8", contentType, "Content-Type이 JSON이어야 함");
        log.info("Content-Type 검증 통과");

        // 응답 본문 검증
        String responseBody = response.getContentAsString();
        log.info("응답 본문 검증 시작: {}", responseBody);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
        
        assertEquals("SUCCESS", responseMap.get("status"), "응답 상태가 SUCCESS여야 함");
        assertNotNull(responseMap.get("body"), "응답 본문이 존재해야 함");
        log.info("응답 본문 검증 통과 - status: {}, body 존재 여부: {}", 
            responseMap.get("status"), responseMap.get("body") != null);

        log.info("=== 토큰 생성 검증 테스트 완료 ===");
    }

    @Test
    @DisplayName("관리자 로그인 성공 시나리오 테스트")
    void successfulAuthentication_Test() throws Exception {
        log.info("=== 관리자 로그인 성공 시나리오 테스트 시작 ===");
        // given
        String username = "admin";
        String password = "password";
        String role = "ROLE_ADMIN";
        String nickname = "관리자";
        String accessToken = "test.access.token";
        String refreshToken = "test.refresh.token";

        log.info("테스트 데이터 설정 - username: {}, role: {}, nickname: {}", username, role, nickname);

        User mockUser = User.builder()
                .username(username)
                .nickname(nickname)
                .role(role)
                .build();
        log.info("Mock 사용자 객체 생성 완료");

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        log.info("인증 객체 생성 완료 - 권한: {}", userDetails.getAuthorities());

        when(jwtUtil.generateAccessToken(username, role, nickname)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(username, role, nickname)).thenReturn(refreshToken);
        log.info("토큰 생성 Mock 설정 완료 - AccessToken: {}, RefreshToken: {}", accessToken, refreshToken);

        // when
        log.info("로그인 프로세스 시작");
        adminLoginFilter.successfulAuthentication(request, response, null, authentication);
        log.info("로그인 프로세스 완료");

        // then
        log.info("=== 검증 시작 ===");

        //1. 헤더 검증
        String authHeader = response.getHeader("Authorization");
        log.info("Authorization 헤더 검증 시작 - 실제 값: {}", authHeader);
        assertNotNull(authHeader, "Authorization 헤더가 존재하지 않음");
        log.info("Authorization 헤더 존재 확인");
        
        assertTrue(authHeader.startsWith("Bearer "), "Bearer 토큰이 아님");
        log.info("Bearer 토큰 형식 확인");
        
        assertEquals("Bearer " + accessToken, authHeader, "Authorization 헤더 값이 일치해야 함");
        log.info("Authorization 헤더 값 일치 확인");

        //2. 쿠키 검증
        log.info("RefreshToken 쿠키 검증 시작");
        Cookie responseCookie = response.getCookie("refreshToken");
        assertNotNull(responseCookie, "refreshToken 쿠키가 존재해야 함");
        log.info("RefreshToken 쿠키 존재 확인");
        
        assertEquals(refreshToken, responseCookie.getValue(), "refreshToken 값이 일치해야 함");
        log.info("RefreshToken 값 일치 확인");
        
        assertTrue(responseCookie.isHttpOnly(), "쿠키는 HttpOnly여야 함");
        assertTrue(responseCookie.getSecure(), "쿠키는 Secure여야 함");
        log.info("쿠키 보안 설정 확인 - HttpOnly: {}, Secure: {}", 
            responseCookie.isHttpOnly(), responseCookie.getSecure());

        //3. RefreshToken DB 저장 검증
        log.info("RefreshToken DB 저장 검증 시작");
        verify(tokenService).saveRefreshToken(username, refreshToken);
        log.info("RefreshToken DB 저장 검증 완료");

        // 응답 본문 검증
        log.info("응답 본문 검증 시작");
        String responseBody = response.getContentAsString();
        log.info("응답 본문: {}", responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        log.info("JSON 파싱 완료");

        // status 검증
        assertEquals("SUCCESS", jsonNode.get("status").asText(), "응답 상태는 SUCCESS여야 함");
        log.info("응답 상태 검증 완료");

        // body 검증
        JsonNode bodyNode = jsonNode.get("body");
        assertEquals(username, bodyNode.get("username").asText(), "username이 일치해야 함");
        assertEquals(nickname, bodyNode.get("nickname").asText(), "nickname이 일치해야 함");
        assertEquals(role, bodyNode.get("role").asText(), "role이 일치해야 함");
        log.info("응답 body 필드 검증 완료 - username: {}, nickname: {}, role: {}", 
            bodyNode.get("username").asText(), 
            bodyNode.get("nickname").asText(), 
            bodyNode.get("role").asText());

        log.info("=== 관리자 로그인 성공 시나리오 테스트 완료 ===");
    }

    @Test
    @DisplayName("관리자 로그인 실패 시나리오 테스트")
    void unsuccessfulAuthentication_Test() throws Exception {
        log.info("=== 관리자 로그인 실패 시나리오 테스트 시작 ===");
        // given
        AuthenticationException authException = new BadCredentialsException("잘못된 인증 정보");
        log.info("테스트용 인증 예외 생성 - 예외 메시지: {}", authException.getMessage());

        // when
        log.info("로그인 실패 처리 시작");
        adminLoginFilter.unsuccessfulAuthentication(request, response, authException);
        log.info("로그인 실패 처리 완료");

        // then
        log.info("=== 검증 시작 ===");

        // 1.HTTP 상태 코드 검증
        log.info("HTTP 상태 코드 검증 시작 - 실제 상태 코드: {}", response.getStatus());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(),
                "HTTP 상태 코드는 401이어야 함");
        log.info("HTTP 상태 코드 검증 완료");

        // 2. 토큰 부재 검증
        log.info("토큰 부재 검증 시작");
        assertNull(response.getHeader("Authorization"),
                "실패 시 Authorization 헤더가 없어야 함");
        log.info("Authorization 헤더 부재 확인");
        
        assertNull(response.getCookie("refreshToken"),
                "실패 시 RefreshToken 쿠키가 없어야 함");
        log.info("RefreshToken 쿠키 부재 확인");

        // 3. 응답 본문 검증
        log.info("응답 본문 검증 시작");
        String responseBody = response.getContentAsString();
        log.info("응답 본문: {}", responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        log.info("JSON 파싱 완료");

        assertEquals("ERROR", jsonNode.get("status").asText(),
                "응답 상태는 ERROR여야 함");
        log.info("응답 상태 검증 완료");
        
        assertEquals("관리자 계정 정보를 확인하세요.",
                jsonNode.get("body").get("message").asText(),
                "에러 메시지가 일치해야 함");
        log.info("에러 메시지 검증 완료");
        
        assertEquals(HttpStatus.UNAUTHORIZED.name(),
                jsonNode.get("body").get("status").asText(),
                "HTTP 상태 코드가 일치해야 함");
        log.info("HTTP 상태 검증 완료");

        // 4. Content-Type 검증
        log.info("Content-Type 검증 시작 - 실제 Content-Type: {}", response.getContentType());
        assertEquals("application/json;charset=UTF-8",
                response.getContentType(),
                "Content-Type이 JSON이어야 함");
        log.info("Content-Type 검증 완료");

        log.info("=== 관리자 로그인 실패 시나리오 테스트 완료 ===");
    }

}

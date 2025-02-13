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
        //given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        String encodedPw = bCryptPasswordEncoder.encode("admin");
        loginRequest.put("password", encodedPw);

        String requestBody = objectMapper.writeValueAsString(loginRequest);
        request.setContent(requestBody.getBytes());

        CustomUserDetails adminDetails = new CustomUserDetails(
                User.builder()
                        .username("admin")
                        .password(encodedPw)
                        .role("ROLE_ADMIN")
                        .nickname("관리자")
                .build()
        );

        log.info("User authorities: {}" , adminDetails.getAuthorities());

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                adminDetails,
                null,
                adminDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(any())).thenReturn(mockAuthentication);

        //when
        Authentication result = adminLoginFilter.attemptAuthentication(request, response);
        //then
        assertNotNull(result);
        assertEquals("admin", result.getName());
        assertTrue(result.getAuthorities().iterator().next().getAuthority().equals("ROLE_ADMIN"));

    }
    @Test
    @DisplayName("관리자 로그인 실패 - 잘못된 요청 형식")
    void attemptAuthentication_Fail_InvalidRequest() throws Exception {
        // given
        String invalidJson = "잘못된 JSON 형식";
        request.setContent(invalidJson.getBytes());

        // when & then
        Exception e = assertThrows(RuntimeException.class, () -> {
            adminLoginFilter.attemptAuthentication(request, response);
        });

        log.error("에러 메세지 : {}",  e.getMessage());
    }

    @Test
    @DisplayName("로그인 성공 후 토큰 생성 검증 ")
    void successfulAuthentication_TokenGeneration() throws Exception {
        // given
        CustomUserDetails adminDetails = new CustomUserDetails(
                User.builder()
                        .username("admin")
                        .password("admin")
                        .role("ROLE_ADMIN")
                        .nickname("관리자")
                        .build()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                adminDetails,
                null,
                adminDetails.getAuthorities()
        );

        when(jwtUtil.generateAccessToken(anyString(), anyString(), anyString()))
                .thenReturn("mock.access.token");
        when(jwtUtil.generateRefreshToken(anyString(), anyString(), anyString()))
                .thenReturn("mock.refresh.token");

        // when

        adminLoginFilter.successfulAuthentication(request,response,null,authentication);


        // then
        // 1. 헤더 검증
        String authHeader = response.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", authHeader);
        assertEquals("Bearer mock.access.token", authHeader);

        // 2. 토큰 저장 검증
        log.info("토큰 저장 검증 시작");
        verify(tokenService).saveRefreshToken("admin", "mock.refresh.token");
        log.info("토큰 저장 검증 완료");

        // 3. 응답 형식 검증
        String contentType = response.getContentType();
        log.info("응답 Content-Type: {}", contentType);
        assertEquals(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8", contentType);

        // 4. 응답 본문 검증
        String responseBody = response.getContentAsString();
        log.info("응답 본문: {}", responseBody);
        assertNotNull(responseBody);

        // JSON 응답 구조 검증
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(responseBody, Map.class);
        log.info("응답 status: {}", responseMap.get("status"));
        log.info("응답 body: {}", responseMap.get("body"));
        assertEquals("SUCCESS", responseMap.get("status"));
        assertNotNull(responseMap.get("body"));
    }


    @Test
    @DisplayName("관리자 로그인 성공 시나리오 테스트")
    void successfulAuthentication_Test() throws Exception {
        // given
        String username = "admin";
        String password = "password";
        String role = "ROLE_ADMIN";
        String nickname = "관리자";
        String accessToken = "test.access.token";
        String refreshToken = "test.refresh.token";

        log.info("테스트 시작: 관리자 로그인 성공 시나리오");

        User mockUser = User.builder()
                .username(username)
                .nickname(nickname)
                .role(role)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(jwtUtil.generateAccessToken(username, role, nickname)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(username, role, nickname)).thenReturn(refreshToken);

        log.info("토큰 생성 Mock 설정 완료 - Access: {}, Refresh: {}", accessToken, refreshToken);

        // when
        log.info("로그인 프로세스 시작");
        adminLoginFilter.successfulAuthentication(request, response, null, authentication);

        // then
        log.info("응답 검증 시작");

        // 쿠키 검증
        Cookie accessTokenCookie = response.getCookie("accessToken");
        assertNotNull(accessTokenCookie, "AccessToken 쿠키가 존재해야 함");
        assertEquals(accessToken, accessTokenCookie.getValue(), "AccessToken 값이 일치해야 함");
        assertTrue(accessTokenCookie.isHttpOnly(), "쿠키는 HttpOnly여야 함");
        assertTrue(accessTokenCookie.getSecure(), "쿠키는 Secure여야 함");

        log.info("쿠키 검증 완료 - HttpOnly: {}, Secure: {}",
                accessTokenCookie.isHttpOnly(), accessTokenCookie.getSecure());

        // 응답 본문 검증 - JsonNode 사용
        String responseBody = response.getContentAsString();
        log.info("응답 본문: {}", responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // status 검증
        assertEquals("SUCCESS", jsonNode.get("status").asText(), "응답 상태는 SUCCESS여야 함");

        // body 검증
        JsonNode bodyNode = jsonNode.get("body");
        assertEquals(username, bodyNode.get("username").asText(), "username이 일치해야 함");
        assertEquals(nickname, bodyNode.get("nickname").asText(), "nickname이 일치해야 함");
        assertEquals(role, bodyNode.get("role").asText(), "role이 일치해야 함");

        log.info("응답 본문 검증 완료");

        // RefreshToken 저장 검증
        verify(tokenService).saveRefreshToken(username, refreshToken);
        log.info("RefreshToken 저장 검증 완료");

        log.info("테스트 완료: 모든 검증 통과");
    }

    @Test
    @DisplayName("관리자 로그인 실패 시나리오 테스트")
    void unsuccessfulAuthentication_Test() throws Exception {
        // given
        AuthenticationException authException = new BadCredentialsException("잘못된 인증 정보");

        log.info("테스트 시작: 관리자 로그인 실패 시나리오");

        // when
        log.info("로그인 실패 프로세스 시작 - 예외 메시지: {}", authException.getMessage());
        ((TestableAdminLoginFilter) adminLoginFilter).unsuccessfulAuthentication(request, response, authException);

        // then
        log.info("응답 검증 시작");

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(),
                "HTTP 상태 코드는 401이어야 함");

        String responseBody = response.getContentAsString();
        log.info("응답 본문: {}", responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertEquals("ERROR", jsonNode.get("status").asText(), "응답 상태는 ERROR여야 함");
        assertEquals("관리자 계정 정보를 확인하세요.",
                jsonNode.get("body").get("message").asText(),
                "에러 메시지가 일치해야 함");

        log.info("테스트 완료: 모든 검증 통과");
    }

}

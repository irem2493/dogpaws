package com.dogpaws.backend.filter;

import com.dogpaws.backend.dto.rim.AdminInfoDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.global.common.ErrorResponse;
import com.dogpaws.backend.service.ajy.TokenService;
import com.dogpaws.backend.service.common.CustomUserDetails;
import com.dogpaws.backend.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AdminLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    private final int ADMIN_COOKIE_EXPIRE_TIME = 60 * 60 * 12; //  JWTUtil.generateAccessToken 의 시간과 동일하게 설정

    public AdminLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
        //LocalDateTime 직렬화(JSON변환) 문제해결
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //관리자 로그인 URL 설정
        setFilterProcessesUrl("/api/admin/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try {
            response.setCharacterEncoding("UTF-8");
            String body = request.getReader().lines().reduce("",(accumulator, actual) -> accumulator + actual);

            Map<String, String> jsonRequest = objectMapper.readValue(body, Map.class);

            String username=jsonRequest.get("username");
            String password=jsonRequest.get("password");

            if(username == null || password == null){
                throw new RuntimeException("관리자 아이디 또는 비밀번호 값이 없음");
            }

            log.info("관리자 로그인 시도 - username(id) : {}", username);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authToken);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            if(!role.equals("ROLE_ADMIN")){
                throw new RuntimeException("관리자 권한이 없는 사용자입니다.");
            }

            return authentication;

        }catch (Exception e){
            throw new RuntimeException("파싱 오류",e);

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("관리자 로그인 시도 ...");

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String nickname = userDetails.getNickname();

        String acessToken = jwtUtil.generateAccessToken(username, role, nickname);
        String refreshToken = jwtUtil.generateRefreshToken(username, role, nickname);

        tokenService.saveRefreshToken(username, refreshToken);

        response.setHeader("Authorization", "Bearer " + acessToken);

        //refreshToken 을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(ADMIN_COOKIE_EXPIRE_TIME);
        response.addCookie(refreshTokenCookie);

        log.info("RefreshToken 쿠키 설정 - 이름: {}, 값: {}, HttpOnly: {}, Secure: {}, Path: {}",
                refreshTokenCookie.getName(),
                refreshTokenCookie.getValue(),
                refreshTokenCookie.isHttpOnly(),
                refreshTokenCookie.getSecure(),
                refreshTokenCookie.getPath()
        );


        AdminInfoDto adminInfo = AdminInfoDto.builder()
                .username(username)
                .nickname(nickname)
                .role(role)
                .build();

        ApiResponse<AdminInfoDto> apiResponse = new ApiResponse<>(
                ApiResponse.ApiStatus.SUCCESS,
                adminInfo,
                true
        );

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        log.info("관리자 로그인 성공 : username = {}, role = {}", username, role);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.error("관리자 로그인 실패: {}", failed.getMessage());
        log.error("로그인 실패: 인증 예외 클래스 - {}", failed.getClass().getName());
        log.error("로그인 실패: 요청 URI - {}", request.getRequestURI());
        log.error("로그인 실패: 요청 IP - {}", request.getRemoteAddr());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = new ErrorResponse(null, "관리자 계정 정보를 확인하세요.", HttpStatus.UNAUTHORIZED);
        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                errorResponse,
                true
        );

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}

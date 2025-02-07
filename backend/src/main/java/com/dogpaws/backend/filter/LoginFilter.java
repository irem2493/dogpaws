package com.dogpaws.backend.filter;

import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.global.common.ErrorResponse;
import com.dogpaws.backend.service.ajy.TokenService;
import com.dogpaws.backend.service.common.CustomUserDetails;
import com.dogpaws.backend.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            response.setCharacterEncoding("UTF-8");
            String body = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> jsonRequest = objectMapper.readValue(body, Map.class);

            String username = jsonRequest.get("username");
            String password = jsonRequest.get("password");
            String userType = jsonRequest.get("userType");

            if (username == null || password == null || userType == null) {
                throw new RuntimeException("Username, Password 또는 UserType이 비었음");
            }

            log.info("username : {} , password  : {} , userType , {} " , username, password, userType);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
            Authentication authentication = authenticationManager.authenticate(authToken);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            if ((!(role.equals("ROLE_USER")))) {
                throw new RuntimeException("잘못된 사용자 유형입니다.");
            }

            return authentication;

        } catch (IOException e) {
            throw new RuntimeException("파싱 오류 ", e);
        }
    }


    /**
     * 로그인 성공 처리
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공이요");

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String nickname = userDetails.getNickname();


        String accessToken = jwtUtil.generateAccessToken(
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getNickname()
        );
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority(),
                userDetails.getNickname());

        // Refresh Token을 DB나 캐시에 저장 (예: Redis)
        tokenService.saveRefreshToken(username, refreshToken);

        // Access Token을 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // Refresh Token을 쿠키에 저장 (Secure 및 HttpOnly 설정 권장)
        Cookie refreshTokenCookie = new Cookie("Refresh-Token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);  // HTTPS 환경에서는 true로 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(12 * 60 * 60);  // 12시간 유효

        response.addCookie(refreshTokenCookie);

        log.info("JWT 쿠키 설정 완료: accessToken={}, refreshToken={}", accessToken, refreshToken);

        // 사용자 정보 응답 (API Response)
        Map<String, String> userInfo = Map.of("username", username, "role", role, "nickname", nickname);
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, userInfo, false);

        // 응답 전송
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        log.info("로그인 성공: username={}, role={}", username, role);
    }

    /**
     * 로그인 실패 처리
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        // 로그인 실패 로그 상세히 기록
        log.error("로그인 실패: 원인 메시지 - {}", failed.getMessage(), failed); // 스택 트레이스 포함
        log.error("로그인 실패: 인증 예외 클래스 - {}", failed.getClass().getName());
        log.error("로그인 실패: 요청 URI - {}", request.getRequestURI());
        log.error("로그인 실패: 요청 IP - {}", request.getRemoteAddr());


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = new ErrorResponse(null, "아이디 또는 비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED);

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                errorResponse,
                false
        );

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

}


package com.dogpaws.backend.filter;

import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.global.common.ErrorResponse;
import com.dogpaws.backend.service.common.CustomUserDetails;
import com.dogpaws.backend.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String token = extractToken(request);

        if (token == null) {
            log.error("유효하지 않은 토큰");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(token)) {
                log.error("토큰이 만료되었습니다");
                sendErrorResponse(response, "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED);
                return;
            }
        } catch (ExpiredJwtException e) {
            log.error("토큰 만료 예외 발생: {}", e.getMessage());
            sendErrorResponse(response, "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED);
            return;
        } catch (Exception e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            sendErrorResponse(response, "유효하지 않은 토큰", HttpStatus.UNAUTHORIZED);
            return;
        }


        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        log.info("JWT 추출  - username: {}, role: {}", username, role);

        try {
            setAuthentication(username, role);
        } catch (IllegalArgumentException e) {
            log.error("인증 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("검증되지 않은 사용자");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 JWT 토큰을 추출
     */
    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.split(" ")[1];
        }
        return null;
    }

    /**
     * username과 role 정보를 사용해 SecurityContext에 인증 정보 저장
     */
    private void setAuthentication(String username, String role) {
        Object entity = createEntity(username, role);

        if ("ROLE_USER".equals(role) && entity instanceof User userEntity) {
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
            saveToSecurityContext(customUserDetails, username, role);

        } else if ("ROLE_ADMIN".equals(role) && entity instanceof User userEntity) {
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
            saveToSecurityContext(customUserDetails, username, role);

        }  else {
            throw new IllegalArgumentException("검증되지 않은 사용자 : " + role);
        }
    }

    /**
     * SecurityContext에 인증 정보를 저장
     */
    private void saveToSecurityContext(CustomUserDetails customUserDetails, String username, String role) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("SecurityContext에 인증 정보 저장 완료: username={}, role={}", username, role);
    }

    /**
     * 역할(role)에 따라 유저 또는 기업 엔티티 생성
     */
    private Object createEntity(String username, String role) {
        if ("ROLE_USER".equals(role) || "ROLE_ADMIN".equals(role)) {
            User userEntity = User.builder()
                    .username(username)
                    .password("temppassword")
                    .role(role)
                    .build();
            return userEntity;

        } else {
            throw new IllegalArgumentException("검증되지 않은 사용자 : " + role);
        }
    }

    /**
     * 에러 응답을 생성하여 클라이언트로 전송
     * TODO: 추후 수정
     */
    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(null, message, status);

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(
                ApiResponse.ApiStatus.ERROR,
                errorResponse,
                false
        );

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}

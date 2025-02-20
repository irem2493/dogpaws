package com.dogpaws.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import com.dogpaws.backend.utils.JWTUtil;
import com.dogpaws.backend.service.ajy.TokenService;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public class TestableAdminLoginFilter extends AdminLoginFilter {

    public TestableAdminLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, TokenService tokenService) {
        super(authenticationManager, jwtUtil, tokenService);
    }

    @Override
    public void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
    }
    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                           AuthenticationException failed) throws IOException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
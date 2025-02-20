package com.dogpaws.backend.service;

import com.dogpaws.backend.entity.ajy.Token;
import com.dogpaws.backend.repository.jpa.ajy.TokenRepository;
import com.dogpaws.backend.service.rim.AdminAuthTokenService;
import com.dogpaws.backend.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthTokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AdminAuthTokenService adminAuthTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReissueAccessToken_Success() {
        String refreshToken = "valid.refresh.token";
        String username = "admin";
        String role = "ROLE_ADMIN";
        String nickname = "AdminUser";
        Integer tokenId = 1;
        Token token = new Token( tokenId, username, refreshToken, LocalDateTime.now());

        when(jwtUtil.isExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);
        when(jwtUtil.getNickname(refreshToken)).thenReturn(nickname);
        when(tokenRepository.findByUsername(username)).thenReturn(Optional.of(token));

        String newAccessToken = adminAuthTokenService.reissueAccessToken(refreshToken);

        assertNotNull(newAccessToken);
        verify(jwtUtil).generateAccessToken(username, role, nickname);
    }

    @Test
    void testReissueAccessToken_InvalidRole() {
        String refreshToken = "valid.refresh.token";
        String username = "user";
        String role = "ROLE_USER";

        when(jwtUtil.isExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);

        assertThrows(SecurityException.class, () -> adminAuthTokenService.reissueAccessToken(refreshToken));
    }

    @Test
    void testReissueAccessToken_ExpiredToken() {
        String refreshToken = "expired.refresh.token";

        when(jwtUtil.isExpired(refreshToken)).thenReturn(true);

        assertThrows(SecurityException.class, () -> adminAuthTokenService.reissueAccessToken(refreshToken));
    }
}
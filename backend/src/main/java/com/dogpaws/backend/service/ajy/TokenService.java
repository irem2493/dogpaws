package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.entity.ajy.Token;
import com.dogpaws.backend.repository.jpa.ajy.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    // Refresh Token 저장 (DB)
    public void saveRefreshToken(String username, String refreshToken) {
        Token token = tokenRepository.findByUsername(username)
                .orElseGet(() -> {
                    Token newToken = new Token();
                    newToken.setUsername(username);
                    return newToken;
                });

        token.setRefreshToken(refreshToken);
        tokenRepository.save(token);
    }

    // Refresh Token 조회 및 검증
    public boolean validateRefreshToken(String username, String refreshToken) {
        return tokenRepository.findByUsername(username)
                .map(tokenEntity -> tokenEntity.getRefreshToken().equals(refreshToken))
                .orElse(false);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(String username) {
        tokenRepository.deleteByUsername(username);
    }
}

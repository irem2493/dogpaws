package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.entity.ajy.Token;
import com.dogpaws.backend.repository.jpa.ajy.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    /**
     * Refresh Token 저장 (DB)
     * 동일한 username이 존재하면 덮어쓰기
     */
    @Transactional
    public void saveRefreshToken(String username, String refreshToken) {
        Token token = tokenRepository.findByUsername(username)
                .orElseGet(() -> {
                    Token newToken = new Token();
                    newToken.setUsername(username);
                    return newToken;
                });

        token.setRefreshToken(refreshToken);
        tokenRepository.save(token);

        System.out.println("✅ Refresh Token 저장 완료: " + refreshToken);
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(String username, String refreshToken) {
        Optional<Token> tokenEntity = tokenRepository.findByUsername(username);

        if (tokenEntity.isPresent()) {
            String storedToken = tokenEntity.get().getRefreshToken();
            boolean isValid = storedToken.equals(refreshToken);

            System.out.println("🔍 Refresh Token 검증: " + isValid);
            return isValid;
        }

        System.out.println("❌ Refresh Token 검증 실패: 사용자 없음");
        return false;
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    @Transactional
    public void deleteRefreshToken(String username) {
        tokenRepository.deleteByUsername(username);
        System.out.println("🗑️ Refresh Token 삭제 완료");
    }
}

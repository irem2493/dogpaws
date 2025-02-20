package com.dogpaws.backend.service.rim;

import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.repository.jpa.ajy.TokenRepository;
import com.dogpaws.backend.service.ajy.TokenService;
import com.dogpaws.backend.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminAuthTokenService extends TokenService {

    private final JWTUtil jwtUtil;

    public AdminAuthTokenService(TokenRepository tokenRepository, JWTUtil jwtUtil) {
        super(tokenRepository);
        this.jwtUtil = jwtUtil;
    }

    /**
     * 관리자 AcessToken 재발급
     */
    public String reissueAccessToken(String refreshToken) {
        log.info("관리자 AccessToken 재발급 시작");

        TokenUserDto userInfo = extractTokenUserInfo(refreshToken);
        String username = userInfo.getUsername();

        validateAdminRole(refreshToken);
        checkTokenExpiration(refreshToken, "RefreshToken", username);

        if (!super.validateRefreshToken(username, refreshToken)) {
            log.error("저장된 RefreshToken과 일치하지 않음 - 관리자: {}", username);
            throw new SecurityException("유효하지 않은 RefreshToken");
        }

        String newAccessToken = jwtUtil.generateAccessToken(
                userInfo.getUsername(),
                userInfo.getRole(),
                userInfo.getNickname()
        );

        log.info("관리자 AccessToken 재발급 완료 - 관리자: {}, 권한 : {} ", userInfo.getUsername(), userInfo.getRole());
        return newAccessToken;
    }

    /**
     * 토큰 만료 검사
     */
    private void checkTokenExpiration(String token, String tokenType, String username) {
        if (jwtUtil.isExpired(token)) {
            log.error("만료된 " + tokenType + "토큰입니다. - 관리자 : {}" , username);
            throw new SecurityException("만료된 " + tokenType  + " 토큰");
        }
    }

    /**
     * 토큰에서 사용자 정보 추출
     */
    public TokenUserDto extractTokenUserInfo(String token) {
        return new TokenUserDto(
                jwtUtil.getUsername(token),
                jwtUtil.getNickname(token),
                jwtUtil.getRole(token));
    }

    /**
     * 관리자 권한 검증
     */
    private void validateAdminRole(String token) {
        String role = jwtUtil.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            log.error(" 관리자 권한이 없는 사용자 입니다. ");
            throw new SecurityException("관리자 권한이 없는 사용자");
        }
    }
}
package com.dogpaws.frontend.utils;

import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TokenUtil {
    private static final String TOKEN_COOKIE_NAME = "Authorization";

    /**
     * 쿠키에서 토큰을 추출하여 반환.
     * 토큰없을 때 이미 webClicent에서 처리해놨기 떄문에
     * 따로 예외처리 필요없음
     * TODO : 추후 상세한 예외 필요하다면 추가될 수 있음
     */

    public static String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static UserDto verifyTokenAndSetSession(String token, ApiRequestService apiRequestService, HttpSession session) {
        if (token == null) {
            log.warn("토큰이 존재하지 않습니다.");
            return null;
        }

        var response = apiRequestService.postDataWithToken("/api/verify-token", null, token);
        if (response.getStatus() == ApiResponse.ApiStatus.SUCCESS && response.getBody() instanceof Map) {
            Map<String, Object> userData = (Map<String, Object>) response.getBody();
            UserDto user = new UserDto();
            user.setUsername((String) userData.get("username"));
            user.setNickname((String) userData.get("nickname"));
            user.setRole((String) userData.get("role"));
            session.setAttribute("user", user);

            log.info("세션에 유저 정보 저장: {}", user);
            return user;
        } else {
            log.warn("유효하지 않은 응답입니다: {}", response);
            return null;
        }
    }

}

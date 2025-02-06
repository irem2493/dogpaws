package com.dogpaws.frontend.utils;

import com.dogpaws.frontend.dto.ajy.UserDto;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private static final String USER_SESSION_KEY = "user";

    /**
     * 세션에서 UserDto를 가져옴
     * @return UserDto 세션에 저장된 유저 정보, 없으면 null
     */
    public static UserDto getUser(HttpSession session) {
        Object attribute = session.getAttribute(USER_SESSION_KEY);
        if (attribute instanceof UserDto) {
            return (UserDto) attribute;
        }
        return null;
    }
}

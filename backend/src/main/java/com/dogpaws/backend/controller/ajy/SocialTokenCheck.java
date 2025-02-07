package com.dogpaws.backend.controller.ajy;


import com.dogpaws.backend.dto.common.TokenUserDto;
import com.dogpaws.backend.utils.JWTUtil;
import com.dogpaws.backend.utils.ajy.SocialTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class SocialTokenCheck {
    private final SocialTokenUtil socialTokenUtil;


    @PostMapping("/verify-social-token")
    public TokenUserDto verifyToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        log.info("Authorization 헤더 값: {}", authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            String username = socialTokenUtil.getUsername(token);
            String role = socialTokenUtil.getRole(token);
            String nickname = socialTokenUtil.getNickname(token);

            TokenUserDto userDto = new TokenUserDto();
            userDto.setUsername(username);
            userDto.setRole(role);
            userDto.setNickname(nickname);

            return userDto;
        }
        log.error("Authorization 헤더가 없거나 올바르지 않습니다.");
        return null;
    }
}

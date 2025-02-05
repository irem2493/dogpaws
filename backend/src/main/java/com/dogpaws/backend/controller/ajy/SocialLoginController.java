package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.value.KakaoValue;
import com.dogpaws.backend.value.NaverValue;
import com.dogpaws.backend.value.GoogleValue;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialLoginController {

    private final KakaoValue kakaoValue;

    private final NaverValue naverValue;
    private final RestTemplate restTemplate = new RestTemplate();

    private final GoogleValue googleValue;

    // 로그인 요청: 사용자 인증 URL로 리디렉션
    @GetMapping("/kakao/login")
    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoValue.getClientId() +
                "&redirect_uri=" + kakaoValue.getRedirectUri()
                +"&prompt=login";
        response.sendRedirect(kakaoLoginUrl);
    }

    @GetMapping("/naver/login")
    public void redirectToNaverLogin(HttpServletResponse response, HttpSession session) throws IOException {
        String state = generateState(session); // CSRF 방지용 state 값 생성
        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + naverValue.getNaverClientId() +
                "&redirect_uri=" + naverValue.getNaverRedirectUri() +
                "&state=" + state;
        response.sendRedirect(naverLoginUrl); // 네이버 로그인 페이지로 리다이렉트
    }

    private String generateState(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state); // 세션에 state 저장
        return state;
    }

  /*  @GetMapping("/google/login")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        String googleLoginUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleValue.getClientId()
                + "&redirect_uri=" + googleValue.getRedirectUri()
                + "&response_type=code"
                + "&scope=openid email profile https://www.googleapis.com/auth/user.gender.read https://www.googleapis.com/auth/user.birthday.read"
                + "&access_type=offline"
                + "&prompt=consent";

        response.sendRedirect(googleLoginUrl);
    }*/

}

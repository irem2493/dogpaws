package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.JoinSessionDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.ajy.SocialLoginService;
import com.dogpaws.backend.service.ajy.TokenService;
import com.dogpaws.backend.service.ajy.UserService;
import com.dogpaws.backend.utils.JWTUtil;
import com.dogpaws.backend.value.KakaoValue;
import com.dogpaws.backend.value.NaverValue;
import com.dogpaws.backend.value.GoogleValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor

public class SocialLoginController {

    private final KakaoValue kakaoValue;

    private final NaverValue naverValue;

    private final GoogleValue googleValue;

    private final SocialLoginService socialLoginService;
    private final RestTemplate restTemplate = new RestTemplate();


    // 로그인 요청: 사용자 인증 URL로 리디렉션
    @GetMapping("/social/kakao/login")
    public String  redirectToKakaoLogin() throws IOException {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + kakaoValue.getClientId() +
                "&redirect_uri=" + kakaoValue.getRedirectUri()
                +"&prompt=login";
        return "redirect:" + kakaoLoginUrl;
    }

    @GetMapping("/social/naver/login")
    public String  redirectToNaverLogin(HttpSession session) throws IOException {
        String state = generateState(session); // CSRF 방지용 state 값 생성
        String naverLoginUrl = "https://nid.naver.com/oauth2.0/authorize" +
                "?response_type=code" +
                "&client_id=" + naverValue.getNaverClientId() +
                "&redirect_uri=" + naverValue.getNaverRedirectUri() +
                "&state=" + state;
        return "redirect:" + naverLoginUrl;
    }

    private String generateState(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state); // 세션에 state 저장
        return state;
    }

    @GetMapping("/social/google/login")
    public String  redirectToGoogleLogin() throws IOException {
        String googleLoginUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleValue.getClientId()
                + "&redirect_uri=" + googleValue.getRedirectUri()
                + "&response_type=code"
                + "&scope=openid email profile https://www.googleapis.com/auth/user.gender.read https://www.googleapis.com/auth/user.birthday.read"
                + "&access_type=offline"
                + "&prompt=consent";

        return "redirect:" + googleLoginUrl;
    }

    // 2. Callback: Authorization Code 수신 -kakao

    @GetMapping("/oauth")
    public String handleOAuthRedirect(@RequestParam String code, HttpSession session, HttpServletResponse response) throws IOException {
       try {
            // 1. Access Token 요청
            String accessToken = socialLoginService.getAccessToken(code);

            // 2. 사용자 정보 요청
            var userResponse = socialLoginService.getUserInfo(accessToken);
            var userInfo = userResponse.getBody();

            //System.out.println(userInfo);

           return socialLoginService.getReturnPage(userInfo, session, response);

        } catch (Exception e) {
            e.printStackTrace();
            //model.addAttribute("error", e.getMessage());
            return "redirect:http://localhost:2000/join";  // 프론트로 리다이렉트
        }
    }

    @GetMapping("/naver/callback")
    public String oauthCallback(
            @RequestParam String code, @RequestParam String state, HttpSession session, HttpServletResponse response) throws IOException {
        try {
            // 1. Access Token 요청
            String accessToken = socialLoginService.getNaverAccessToken(code, state);

            // 2. 사용자 정보 요청
            var userResponse = socialLoginService.getNaverUserInfo(accessToken);
            var userInfo = userResponse.getBody();

            //System.out.println(userInfo);

            return socialLoginService.getReturnPage(userInfo, session, response);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:http://localhost:2000/join";  // 프론트로 리다이렉트
        }
    }

    @GetMapping("/auth/callback")
    public String handleGoogleCallback(@RequestParam String code,HttpSession session, HttpServletResponse response) {
        try {
            // 1. 액세스 토큰 요청
            String accessToken = getGoogleAccessToken(code);

            // 2. 사용자 정보 요청
            var userInfo = socialLoginService.getGoogleUserInfo(accessToken);
            System.out.println(userInfo);
            System.out.println(socialLoginService.getReturnPage(userInfo, session, response));
            //System.out.println(userInfo);
            return socialLoginService.getReturnPage(userInfo.getBody(), session, response);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:http://localhost:2000/join";  // 프론트로 리다이렉트
        }
    }

    private String getGoogleAccessToken(String code) {
        String tokenUri = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleValue.getClientId());
        params.add("client_secret", googleValue.getClientSecret());
        params.add("redirect_uri", googleValue.getRedirectUri());
        params.add("grant_type", "authorization_code");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

}

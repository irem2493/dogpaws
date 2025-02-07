package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.value.KakaoValue;
import com.dogpaws.backend.value.NaverValue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final KakaoValue kakaoValue;
    private final RestTemplate restTemplate = new RestTemplate();

    private final UserService userService;

    // Access Token 요청
    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Access Token 요청 파라미터
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoValue.getClientId());
        params.add("redirect_uri", kakaoValue.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            //Spring RestTemplate의 메서드로, 서버에서 다른 서버로 POST 요청을 보낼 때 사용
            ResponseEntity<Map> response = restTemplate.postForEntity(kakaoValue.getTokenUri(), request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {

                System.out.println(response.getBody());
                Map<String, Object> responseBody = response.getBody();
                return (String) responseBody.get("access_token");
            } else {
                throw new RuntimeException("Failed to fetch access token: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while requesting access token: " + e.getMessage(), e);
        }
    }

    // 사용자 정보 요청
    public UserRequestDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Bearer 인증 설정

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(kakaoValue.getUserInfoUri(), HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {

                System.out.println(response.getBody());

                String username = response.getBody().get("id").toString();
                //System.out.println("username : "+username);

                // 2. kakao_account 정보 파싱
                // 사용자 정보 파싱

                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");

                User user = userService.findByUsername(username);
                if(user == null) {
                    UserRequestDto userRequestDto = new UserRequestDto();
                    userRequestDto.setUsername(username);
                    userRequestDto.setProvider("kakao");
                    userRequestDto.setNickname(kakaoAccount.get("name").toString());
                    userRequestDto.setEmail(kakaoAccount.get("email").toString());

                    if(kakaoAccount.get("age_range").toString() != null) {
                        if(kakaoAccount.get("age_range").toString().equals("20~29"))
                            userRequestDto.setAgeGroup("20대");
                        else if(kakaoAccount.get("age_range").toString().equals("30~39"))
                            userRequestDto.setAgeGroup("30대");
                    }

                    if(kakaoAccount.get("gender").toString().equals("male")) {
                        userRequestDto.setGender("M");
                    }else{
                        userRequestDto.setGender("F");
                    }

                    return userRequestDto;
                }
                return null; // 사용자가 회원등록한 회원인 경우
            } else {
                throw new RuntimeException("Failed to fetch user info: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while requesting user info: " + e.getMessage(), e);
        }
    }
}

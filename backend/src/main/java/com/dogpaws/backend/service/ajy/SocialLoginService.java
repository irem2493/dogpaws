package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.JoinSessionDto;
import com.dogpaws.backend.dto.ajy.UserRequestDto;
import com.dogpaws.backend.entity.ajy.User;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.utils.JWTUtil;
import com.dogpaws.backend.value.KakaoValue;
import com.dogpaws.backend.value.NaverValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService {

    private final KakaoValue kakaoValue;
    private final NaverValue naverValue;
    private final RestTemplate restTemplate = new RestTemplate();

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

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
    public ApiResponse<?> getUserInfo(String accessToken) {
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
                    userRequestDto.setProvider("KAKAO");
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

                    return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, userRequestDto);
                }
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, user);
            } else {
                throw new RuntimeException("Failed to fetch user info: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while requesting user info: " + e.getMessage(), e);
        }
    }

    // Access Token 요청
    public String getNaverAccessToken(String code, String state) {
        String url = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverValue.getNaverClientId());
        params.add("client_secret", naverValue.getNaverClientSecret());
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            return (String) body.get("access_token");
        } else {
            throw new RuntimeException("Failed to get Access Token");
        }
    }

    // 사용자 정보 요청
    public ApiResponse<?> getNaverUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> naverAccount =  (Map<String, Object>) response.getBody().get("response");
            //System.out.println(naverAccount);

            String username = naverAccount.get("id").toString();
            User user = userService.findByUsername(username);

            if(user == null) {
                UserRequestDto userRequestDto = new UserRequestDto();
                userRequestDto.setUsername(username);
                userRequestDto.setProvider("NAVER");
                userRequestDto.setNickname(naverAccount.get("name").toString());
                userRequestDto.setEmail(naverAccount.get("email").toString());
                userRequestDto.setGender(naverAccount.get("gender").toString());

                if(naverAccount.get("age").toString() != null) {
                    if(naverAccount.get("age").toString().equals("20~29"))
                        userRequestDto.setAgeGroup("20대");
                    else if(naverAccount.get("age").toString().equals("30~39"))
                        userRequestDto.setAgeGroup("30대");
                }
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, userRequestDto);

            }else{
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, user);
            }

        } else {
            throw new RuntimeException("Failed to get user info");
        }
    }

    public ApiResponse<?> getGoogleUserInfo(String accessToken) {
        String userInfoUri = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> googleAccount = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, Map.class);
        Map<String, Object> userInfo =  (Map<String, Object>)googleAccount.getBody();

        Map<String, Object> googleAccount2 = getGoogleUserInfo2(accessToken);
        Map<String, String> userInfo2 = extractUserInfo(googleAccount2);

        if (googleAccount.getStatusCode() == HttpStatus.OK) {
            // 응답 바디에서 ID 추출

            String username = userInfo.get("id").toString();
            User user = userService.findByUsername(username);

            if(user == null) {
                UserRequestDto userRequestDto = new UserRequestDto();
                userRequestDto.setUsername(username);
                userRequestDto.setProvider("Google");
                userRequestDto.setNickname(userInfo.get("name").toString());
                userRequestDto.setEmail(userInfo.get("email").toString());

                if(!userInfo2.get("gender").equals("Unknown") && userInfo2.get("gender").equals("male")) {
                    userRequestDto.setGender("M");
                }else{
                    userRequestDto.setGender("F");
                }

                if(!userInfo2.get("ageGroup").equals("Unknown")) {
                    userRequestDto.setAgeGroup(userInfo2.get("ageGroup"));
                }

                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, userRequestDto);
            }else{
                return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, user);
            }
        }else {
            throw new RuntimeException("Failed to get user info");
        }
    }

    private Map<String, Object> getGoogleUserInfo2(String accessToken) {
        String url = "https://people.googleapis.com/v1/people/me?personFields=genders,birthdays";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("google"+response.getBody());
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch user info: " + response.getBody());
        }
    }

    // API에서 받아온 유저 데이터를 기반으로 젠더 및 연령대를 가져오는 메서드
    private Map<String, String> extractUserInfo(Map<String, Object> googleUserData) {
        Map<String, String> userInfoMap = new HashMap<>();

        if (googleUserData == null || googleUserData.isEmpty()) {
            System.out.println("유저 데이터를 불러올 수 없습니다.");
            userInfoMap.put("gender", "Unknown");
            userInfoMap.put("ageGroup", "Unknown");
            return userInfoMap;
        }

        // 🔹 젠더(Gender) 추출
        String gender = extractGender(googleUserData);
        userInfoMap.put("gender", gender);

        // 🔹 연령대(Age Group) 추출
        String ageGroup = extractAgeGroup(googleUserData);
        userInfoMap.put("ageGroup", ageGroup);

        return userInfoMap;
    }

    // ✅ 젠더(Gender) 가져오기 (안전한 형 변환 포함)
    private String extractGender(Map<String, Object> data) {
        if (data.containsKey("genders")) {
            List<?> genders = (List<?>) data.get("genders");
            if (!genders.isEmpty() && genders.get(0) instanceof Map) {
                Map<?, ?> genderMap = (Map<?, ?>) genders.get(0);
                Object genderValue = genderMap.get("value");

                if (genderValue instanceof String) {
                    return (String) genderValue;
                }
            }
        }
        return "Unknown"; // 데이터가 없을 경우 기본값
    }

    // ✅ 연령대(Age Group) 가져오기 (안전한 형 변환 포함)
    private String extractAgeGroup(Map<String, Object> data) {
        if (data.containsKey("birthdays")) {
            List<?> birthdays = (List<?>) data.get("birthdays");
            for (Object birthdayObj : birthdays) {
                if (birthdayObj instanceof Map) {
                    Map<?, ?> birthdayMap = (Map<?, ?>) birthdayObj;
                    if (birthdayMap.containsKey("date") && birthdayMap.get("date") instanceof Map) {
                        Map<?, ?> dateMap = (Map<?, ?>) birthdayMap.get("date");
                        Object yearObj = dateMap.get("year");

                        if (yearObj instanceof Integer) {
                            int birthYear = (Integer) yearObj;
                            int currentYear = java.time.Year.now().getValue();
                            int age = currentYear - birthYear;

                            // 연령대 구분
                            if (age < 20) return "10대";
                            else if (age < 30) return "20대";
                            else if (age < 40) return "30대";
                            else if (age < 50) return "40대";
                            else if (age < 60) return "50대";
                            else return "60대 이상";
                        }
                    }
                }
            }
        }
        return "Unknown"; // 생년월일 데이터가 없을 경우 기본값
    }

    public String getReturnPage(Object userInfo, HttpSession session, HttpServletResponse response) throws IOException {
        if(userInfo instanceof UserRequestDto) {
            UserRequestDto userRequestDto = (UserRequestDto) userInfo;

            JoinSessionDto sessionData = (JoinSessionDto) session.getAttribute("joinSession");

            if (sessionData == null) {
                sessionData = new JoinSessionDto();
            }

            sessionData.setStep1Data(userRequestDto);
            session.setAttribute("joinSession", sessionData);

            return "redirect:http://localhost:2000/socialJoin";  // 프론트로 리다이렉트
        }
        else if(userInfo instanceof User){
            //여기에 토큰 발급하는 로직 필요

            User user = (User) userInfo;

            String accessToken2 = jwtUtil.generateAccessToken(user.getUsername(), "ROLE_USER", user.getNickname());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), "ROLE_USER", user.getNickname());


            // Refresh Token을 DB나 캐시에 저장 (예: Redis)
            tokenService.saveRefreshToken(user.getUsername(), refreshToken);

            // Access Token을 헤더에 추가
            response.setHeader("Authorization", "Bearer " + accessToken2);

            // Refresh Token을 쿠키에 저장 (Secure 및 HttpOnly 설정 권장)
            Cookie refreshTokenCookie = new Cookie("Refresh-Token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);  // HTTPS 환경에서는 true로 설정
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(12 * 60 * 60);  // 12시간 유효

            response.addCookie(refreshTokenCookie);

            log.info("JWT 쿠키 설정 완료: accessToken={}, refreshToken={}", accessToken2, refreshToken);

            System.out.println(userInfo);
            System.out.println(accessToken2);

            // ✅ 프론트엔드로 전달할 사용자 정보
            String redirectUrl = String.format(
                    "http://localhost:2000/dog/dogProfileSelect?accessToken=%s&username=%s&role=%s&nickname=%s",
                    accessToken2,
                    user.getUsername(),
                    "ROLE_USER",
                    URLEncoder.encode(user.getNickname(), "UTF-8")
            );

            log.info("✅ 로그인 성공: username={}, role={}", user.getUsername(), "ROLE_USER");

            return "redirect:" + redirectUrl;  // ✅ 프론트엔드로 토큰과 사용자 정보를 전달

        }else return  null;
    }
}

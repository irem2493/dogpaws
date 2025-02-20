package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.service.ApiRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final ApiRequestService apiService;

    @GetMapping("/join")
    public String join(Model model, HttpSession session) {
        var providerResponse = apiService.fetchData("/api/join/social/provider");
        var providerResponseBody = providerResponse.getBody();

        // providerResponseBody가 Map인지 확인 후 변환
        if (!(providerResponseBody instanceof Map)) {
            return "/ajy/join"; // 예상한 JSON 형태가 아니면 기본 경로 반환
        }

        Map<String, Object> bodyMap = (Map<String, Object>) providerResponseBody;

        // "body" 값이 존재하는지 확인
        Object innerBodyObj = bodyMap.get("body");
        if (!(innerBodyObj instanceof Map)) {
            return "/ajy/join"; // body가 Map 형태가 아니면 기본 경로 반환
        }

        Map<String, Object> provider = (Map<String, Object>) innerBodyObj;

        // 내부 "status" 값 가져오기
        Object statusObj = provider.get("status");
        if (!(statusObj instanceof String)) {
            return "/ajy/join"; // status가 String이 아니면 기본 경로 반환
        }

        String innerStatus = (String) statusObj;

        if ("SUCCESS".equals(innerStatus)) {
            return "/ajy/join_social_address";
        }

        return "/ajy/join";
    }


    @GetMapping("/dogprofile")
    public String dogprofile(Model model) {
        var breedResponse = apiService.fetchData("/api/gubn/breed_code");
        var personalityResponse = apiService.fetchData("/api/gubn/dog_personal_code");
        var playResponse = apiService.fetchData("/api/gubn/dog_play_code");

        var breedList = breedResponse.getBody();
        var personalityList = personalityResponse.getBody();
        var playList = playResponse.getBody();

        //System.out.println(personalityResponse.getBody());

        model.addAttribute("breedList", breedList);
        model.addAttribute("personalityList", personalityList);
        model.addAttribute("playList", playList);
        return "/ajy/dog_profile";
    }

    @GetMapping("/matching_select")
    public String matching_select() {
        return "/ajy/matching_select";
    }

    @GetMapping("/socialJoin")
    public String socialJoin() {
        return "/ajy/join_social_address";
    }
}

package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final ApiRequestService apiService;

    @GetMapping("/join")
    public String join(Model model) {
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

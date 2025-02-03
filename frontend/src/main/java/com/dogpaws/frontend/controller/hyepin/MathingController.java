package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MathingController {

    private final ApiRequestService apiService;

    @GetMapping
    public String friendMatchingForm(Model model) {
        var breedResponse = apiService.fetchData("/api/gubn/breed_code");
        var personalityResponse = apiService.fetchData("/api/gubn/dog_personal_code");
        var playResponse = apiService.fetchData("/api/gubn/dog_play_code");
        var dogTypeCodeResponse = apiService.fetchData("/api/gubn/dog_type_code");

        var breedList = breedResponse.getBody();
        var personalityList = personalityResponse.getBody();
        var playList = playResponse.getBody();
        var dogTypeCodeList = dogTypeCodeResponse.getBody();

        //System.out.println(personalityResponse.getBody());

        model.addAttribute("breedList", breedList);
        model.addAttribute("personalityList", personalityList);
        model.addAttribute("playList", playList);
        model.addAttribute("dogTypeCodeList", dogTypeCodeList);
        return "hyepin/friend-matching";
    }

}

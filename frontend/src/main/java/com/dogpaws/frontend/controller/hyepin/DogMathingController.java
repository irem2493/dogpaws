package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/matching")
@Slf4j
public class DogMathingController {

    private final ApiRequestService apiService;

    @GetMapping
    public String friendMatchingForm(Model model) {

        //Map<String, String> 변환
        Map<String, String> matchingFilterMap = Map.of(
                "dogId", "1",
                "matchType", "F"
        );

        var filterResponse = apiService.fetchData("/api/matching/filter", matchingFilterMap, true);
        var breedResponse = apiService.fetchData("/api/gubn/breed_code");
        var personalityResponse = apiService.fetchData("/api/gubn/dog_personal_code");
        var playResponse = apiService.fetchData("/api/gubn/dog_play_code");
        var dogTypeCodeResponse = apiService.fetchData("/api/gubn/dog_type_code");

        var filter = filterResponse.getBody();
        var breedList = breedResponse.getBody();
        var personalityList = personalityResponse.getBody();
        var playList = playResponse.getBody();
        var dogTypeCodeList = dogTypeCodeResponse.getBody();

        model.addAttribute("filter", filter);
        model.addAttribute("breedList", breedList);
        model.addAttribute("personalityList", personalityList);
        model.addAttribute("playList", playList);
        model.addAttribute("dogTypeCodeList", dogTypeCodeList);
        return "hyepin/friend-matching";
    }

}

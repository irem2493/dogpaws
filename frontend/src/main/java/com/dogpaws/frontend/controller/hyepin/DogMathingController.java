package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/matching")
@Slf4j
public class DogMathingController {

    private final ApiRequestService apiService;

    @GetMapping("/friend")
    public String friendMatchingForm(Model model, HttpSession session) {

        DogDto dog = (DogDto) session.getAttribute("dog");
        if(dog != null){


        String dogId = String.valueOf(dog.getDogId());
        //Map<String, String> 변환
        Map<String, String> matchingFilterMap = Map.of(
                "dogId", dogId,
                "matchType", "F"
        );

        var filterResponse = apiService.fetchData("/api/matching/filter", matchingFilterMap, true);
        var breedResponse = apiService.fetchData("/api/gubn/breed_code");
            var personalityResponse = apiService.fetchData("/api/gubn/dog_personal_code");
            var playResponse = apiService.fetchData("/api/gubn/dog_play_code");
            var dogTypeCodeResponse = apiService.fetchData("/api/gubn/dog_type_code");

            var filter = filterResponse.getBody();
            System.out.println("filter" + filter);
            var breedList = breedResponse.getBody();
            var personalityList = personalityResponse.getBody();
            var playList = playResponse.getBody();
            var dogTypeCodeList = dogTypeCodeResponse.getBody();

            model.addAttribute("filter", filter);
            model.addAttribute("breedList", breedList);
            model.addAttribute("personalityList", personalityList);
            model.addAttribute("playList", playList);
            model.addAttribute("dogTypeCodeList", dogTypeCodeList);
            return "hyepin/friend_matching";
        }
        else{
            return "redirect:/login";
        }
    }

    @GetMapping("/partner")
    public String partnerMatchingForm(Model model, HttpSession session) {

        DogDto dog = (DogDto) session.getAttribute("dog");
        if(dog != null) {
            String dogId = String.valueOf(dog.getDogId());
            //Map<String, String> 변환
            Map<String, String> matchingFilterMap = Map.of(
                    "dogId", dogId,
                    "matchType", "P"
            );

            ApiResponse dogResponse = apiService.fetchData("/api/matching/detail/" + dogId);

            //필터가 아니라, 활성화가 안돼있을 때임. 다시!
            if (dogResponse != null && dogResponse.getStatus() == ApiResponse.ApiStatus.SUCCESS) {
                Boolean isMatingAvailable = (Boolean) dogResponse.getBody(); // Boolean 값 가져오기

                // 교배 매칭 가능 여부 체크 후 뷰 반환
                if (Boolean.TRUE.equals(isMatingAvailable)) {
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
                    return "hyepin/partner_matching"; // 매칭 가능 페이지
                } else {
                    return "hyepin/non_filter_partner_matching"; // 매칭 불가능 페이지
                }
            }
            return "hyepin/non_filter_partner_matching";
        }
        return "redirect:/login";
    }

}

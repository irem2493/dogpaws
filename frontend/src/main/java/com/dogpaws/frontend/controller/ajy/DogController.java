package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.SessionUtil;
import com.dogpaws.frontend.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class DogController {

    private final ApiRequestService apiRequestService;
    private final ApiRequestService apiService;

    @GetMapping("/dogProfileSelect")
    public String dogProfileSelect(HttpServletRequest request, HttpSession session) {
        String token = TokenUtil.getTokenFromCookies(request);

       // System.out.println("강아지 리스트 토큰 : "+ token);

        UserDto userDto = TokenUtil.verifyTokenAndSetSession(token, apiRequestService, session, request);

        System.out.println("강아지 리스트 요청 : " + userDto);

        if (userDto != null) {
            var dogResponse = apiRequestService.fetchData("/api/dog/dogList/" + userDto.getUsername());
            if (dogResponse != null && dogResponse.getBody() != null) {
                System.out.println("dogList : " + dogResponse.getBody());
                session.setAttribute("dogList", dogResponse.getBody());
            } else {
                // 오류 처리 또는 디폴트 동작 설정
                session.removeAttribute("dogList");
            }
        }

        return "/ajy/dog_profile_select";
    }

    @PostMapping("/saveProfile")
    @ResponseBody
    public ResponseEntity<String> saveProfile(HttpSession session, @RequestBody DogDto dogDto) {
        System.out.println("강아지 프로필 저장 : " + dogDto);
        if (dogDto != null) {
            session.setAttribute("dog", dogDto);
            log.info("세션에 강아지 정보 저장: {}", dogDto);
            return ResponseEntity.ok("프로필 선택 완료");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/dogProfileRegister")
    public String dogProfileRegister(Model model) {

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

        return "/ajy/dog_profile_register";
    }
    
    //마이페이지 -강아지 리스트 요청
    @GetMapping("/mypage/dogList")
    public String dogList(Model model, HttpSession session) {

        if(SessionUtil.getUser(session) != null){
            var dogListResponse = apiService.fetchData("/api/dog/mypage/dogList/"+ Objects.requireNonNull(SessionUtil.getUser(session)).getUsername());
            var dogList = dogListResponse.getBody();

            var breedResponse = apiService.fetchData("/api/gubn/breed_code");
            var personalityResponse = apiService.fetchData("/api/gubn/dog_personal_code");
            var playResponse = apiService.fetchData("/api/gubn/dog_play_code");

            var breedList = breedResponse.getBody();
            var personalityList = personalityResponse.getBody();
            var playList = playResponse.getBody();

            System.out.println("dogList Mypage : " + dogList);
            model.addAttribute("dogList", dogList);
            model.addAttribute("breedList", breedList);
            model.addAttribute("personalityList", personalityList);
            model.addAttribute("playList", playList);
            return "/ajy/mypage/doglist";
        }
        return "redirect:/login";
    }
}

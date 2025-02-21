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
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/dog")
public class DogController {

    private final ApiRequestService apiRequestService;

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

    //강아지 등록
    @GetMapping("/dogProfileRegister")
    public String dogProfileStep1(Model model) {

        var breedResponse = apiRequestService.fetchData("/api/gubn/breed_code");
        var personalityResponse = apiRequestService.fetchData("/api/gubn/dog_personal_code");
        var playResponse = apiRequestService.fetchData("/api/gubn/dog_play_code");

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
            var dogListResponse = apiRequestService.fetchData("/api/dog/mypage/dogList/"+ Objects.requireNonNull(SessionUtil.getUser(session)).getUsername());
            var dogList = dogListResponse.getBody();

            var breedResponse = apiRequestService.fetchData("/api/gubn/breed_code");
            var personalityResponse = apiRequestService.fetchData("/api/gubn/dog_personal_code");
            var playResponse = apiRequestService.fetchData("/api/gubn/dog_play_code");

            var breedList = breedResponse.getBody();
            var personalityList = personalityResponse.getBody();
            var playList = playResponse.getBody();

            //System.out.println("dogList Mypage : " + dogList);
            model.addAttribute("dogList", dogList);
            model.addAttribute("breedList", breedList);
            model.addAttribute("personalityList", personalityList);
            model.addAttribute("playList", playList);
            return "/ajy/mypage/doglist";
        }
        return "redirect:/login";
    }

    //강아지 상세 정보
    @GetMapping("/detail/{dogId}")
    public String detail(@PathVariable("dogId") Integer dogId, Model model, HttpSession session) {
        UserDto user = SessionUtil.getUser(session);

        if(user != null){

            DogDto sessionDog = (DogDto) session.getAttribute("dog");
            if(sessionDog != null){
                int myDogId = sessionDog.getDogId();

                var dogResponse = apiRequestService.fetchData("/api/dog/detail/" + dogId + "/" + myDogId);
                var dog = dogResponse.getBody();

                var breedResponse = apiRequestService.fetchData("/api/gubn/breed_code");
                var breedList = breedResponse.getBody();

                var personalityResponse = apiRequestService.fetchData("/api/gubn/dog_personal_code");
                var playResponse = apiRequestService.fetchData("/api/gubn/dog_play_code");
                var dogTypeResponse = apiRequestService.fetchData("/api/gubn/dog_type_code");

                var personalityList = personalityResponse.getBody();
                var playList = playResponse.getBody();
                var dogTypeList = dogTypeResponse.getBody();

                //System.out.println("강아지 상세정보 : " + dog);

                model.addAttribute("username", user.getUsername());
                model.addAttribute("dog", dog);
                model.addAttribute("breedList", breedList);
                model.addAttribute("personalityList", personalityList);
                model.addAttribute("playList", playList);
                model.addAttribute("dogTypeList", dogTypeList);

                return "/ajy/dog_detail";
            }
            else return "redirect:/dog/dogProfileSelect";
        }
        return "redirect:/login";
    }

    @GetMapping("/dogEidt/{dogId}")
    public String dogEidt(@PathVariable("dogId") Integer dogId, Model model, HttpSession session) {

        UserDto user = SessionUtil.getUser(session);

        if(user != null){
            DogDto sessionDog = (DogDto) session.getAttribute("dog");

            if(sessionDog != null ){

                int myDogId = sessionDog.getDogId();

                var dogResponse = apiRequestService.fetchData("/api/dog/detail/" + dogId + "/" + myDogId);
                var dog = dogResponse.getBody();

                var breedResponse = apiRequestService.fetchData("/api/gubn/breed_code");
                var breedList = breedResponse.getBody();

                var personalityResponse = apiRequestService.fetchData("/api/gubn/dog_personal_code");
                var playResponse = apiRequestService.fetchData("/api/gubn/dog_play_code");
                var dogTypeResponse = apiRequestService.fetchData("/api/gubn/dog_type_code");

                var personalityList = personalityResponse.getBody();
                var playList = playResponse.getBody();
                var dogTypeList = dogTypeResponse.getBody();

                model.addAttribute("username", user.getUsername());

                //System.out.println("강아지 상세정보 : " + dog);

                model.addAttribute("dog", dog);
                model.addAttribute("breedList", breedList);
                model.addAttribute("personalityList", personalityList);
                model.addAttribute("playList", playList);
                model.addAttribute("dogTypeList", dogTypeList);

                return "/ajy/dog_profile_register";
            }else return "redirect:/dog/dogProfileSelect";
        }
        return "redirect:/login";
    }

    @GetMapping("/nearbyDogMap")
    public String nearbyDogMap(Model model, HttpSession session) {
        UserDto user = SessionUtil.getUser(session);
        if(user != null){
            var locationResponse = apiRequestService.fetchData("/api/user/location/" + user.getUsername());
            var coordinates = locationResponse.getBody();

            System.out.println(coordinates);

            if(session.getAttribute("dog") != null){
                model.addAttribute("coordinates", coordinates);
                model.addAttribute("username", user.getUsername());
                return "/ajy/near_dog_map";
            }

            else return "redirect:/dog/dogProfileSelect";
        }
        return "redirect:/login";
    }


}

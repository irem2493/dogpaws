package com.dogpaws.frontend.controller;


import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {

    private final ApiRequestService apiService;

    public MainController(ApiRequestService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {

        DogDto dog = (DogDto) session.getAttribute("dog");
        UserDto user = (UserDto) session.getAttribute("user");

        if (user != null) {
            var dogResponse = apiService.fetchData("/api/main/profile/" + dog.getDogId());
            if (dogResponse != null && dogResponse.getBody() != null) {
                System.out.println("front dogResponse : "+dogResponse.getBody());
                model.addAttribute("dogProfile", dogResponse.getBody());
            }
        }

        model.addAttribute("dog", dog);
        model.addAttribute("user", user);
        return "index";
    }

}


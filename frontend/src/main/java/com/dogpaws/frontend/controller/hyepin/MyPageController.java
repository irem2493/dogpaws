package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final ApiRequestService apiService;

    @GetMapping
    public String mypage() {
        return "hyepin/mypage-mobile";
    }



}
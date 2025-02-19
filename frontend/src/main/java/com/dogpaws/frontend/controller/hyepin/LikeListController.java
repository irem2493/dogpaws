package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.service.ApiRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeListController {

    private final ApiRequestService apiService;

    @GetMapping
    public String likeList() {
        return "hyepin/like_list";
    }



}
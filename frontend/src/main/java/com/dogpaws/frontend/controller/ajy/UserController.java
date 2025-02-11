package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/editUserPassword")
    public String login(HttpServletRequest request, HttpSession session, Model model) {

        String token = TokenUtil.getTokenFromCookies(request);

        // System.out.println("강아지 리스트 토큰 : "+ token);

        UserDto userDto = TokenUtil.verifyTokenAndSetSession(token, apiRequestService, session, request);

        var providerResponse = apiRequestService.fetchData("/api/join/social/provider");
        var provider = providerResponse.getBody();

        if(userDto != null) {
            model.addAttribute("user", userDto);
            return "/ajy/user_edit_password";
        }

        if(provider != null) {
            return "/ajy/user_edit_social";
        }

        return "redirect:/login";
    }



}

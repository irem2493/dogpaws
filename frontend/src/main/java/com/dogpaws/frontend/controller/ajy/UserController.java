package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.ajy.UserResponseDto;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.SessionUtil;
import com.dogpaws.frontend.utils.TokenUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/editUserPassword")
    public String login(HttpServletRequest request, HttpSession session, Model model) {

        UserDto userDto = SessionUtil.getUser(session);

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

    @PostMapping("/editUser")
    public String editUser(@RequestParam("userData") String userData, Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 알 수 없는 속성 무시 설정

            UserResponseDto userDto = objectMapper.readValue(userData, UserResponseDto.class);
            System.out.println("editUser : " + userDto);

            model.addAttribute("user", userDto);
            return "/ajy/user_edit";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}

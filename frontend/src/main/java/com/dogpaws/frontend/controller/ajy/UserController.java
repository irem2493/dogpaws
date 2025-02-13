package com.dogpaws.frontend.controller.ajy;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.dto.ajy.UserResponseDto;
import com.dogpaws.frontend.global.ApiResponse;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.SessionUtil;
import com.dogpaws.frontend.utils.TokenUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/editUserPassword")
    public String login(HttpServletRequest request, HttpSession session, Model model, ModelMap modelMap) {

        UserDto userDto = SessionUtil.getUser(session);

        if (userDto != null) {
            var apiResponse = apiRequestService.fetchData("/api/user/" + userDto.getUsername());
            Map<String, Object> responseMap = (Map<String, Object>) apiResponse.getBody(); // ✅ 응답 전체 Map으로 변환

            System.out.println("user : " + responseMap);

            if (responseMap != null && responseMap.containsKey("body")) {
                Map<String, Object> userBody = (Map<String, Object>) responseMap.get("body"); // ✅ `body` 부분 가져오기

                if (userBody != null && userBody.containsKey("provider")) {
                    Object provider = userBody.get("provider"); // ✅ provider 값 추출
                    System.out.println("Provider: " + provider);

                    if (provider != null && !provider.toString().trim().isEmpty()) { // ✅ null 또는 빈 문자열 방지
                        model.addAttribute("provider", provider);
                        return "/ajy/user_edit_email";
                    }
                }
            }

            model.addAttribute("user", userDto);
            return "/ajy/user_edit_password";
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

    @PostMapping("/editUserSocial")
    public String editUserSocial(@RequestParam("userData") String userData, Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  // 알 수 없는 속성 무시 설정

            UserResponseDto userDto = objectMapper.readValue(userData, UserResponseDto.class);
            System.out.println("editUserSocial : " + userDto);

            model.addAttribute("user", userDto);
            return "/ajy/user_edit_social";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}

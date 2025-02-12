package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.dto.ajy.UserDto;
import com.dogpaws.frontend.service.ApiRequestService;
import com.dogpaws.frontend.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final ApiRequestService apiService;

    @GetMapping
    public String calendarForm(Model model, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        System.out.println("강아지 리스트 요청 : " + userDto);
        if (userDto != null) {
            var dogResponse = apiService.fetchData("/api/dog/dogList/" + userDto.getUsername());
            if (dogResponse != null && dogResponse.getBody() != null) {
                model.addAttribute("dogList", dogResponse.getBody());
            }
        }
        return "hyepin/calendar";
    }
}


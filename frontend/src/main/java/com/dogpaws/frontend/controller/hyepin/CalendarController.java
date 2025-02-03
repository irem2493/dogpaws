package com.dogpaws.frontend.controller.hyepin;

import com.dogpaws.frontend.service.ApiRequestService;
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
    public String calendarForm(Model model) {

        return "hyepin/calendar";
    }

}

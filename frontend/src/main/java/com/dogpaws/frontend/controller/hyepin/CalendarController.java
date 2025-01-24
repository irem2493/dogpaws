package com.dogpaws.frontend.controller.hyepin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String index() {
        return "hyepin/calendar";
    }

}

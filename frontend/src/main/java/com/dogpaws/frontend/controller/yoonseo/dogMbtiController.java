package com.dogpaws.frontend.controller.yoonseo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on 2025-01-27 by 최윤서
 */
@Controller
public class dogMbtiController {

    @RequestMapping("/dbti-main")
    public String dogMbti() {return "yoonseo/dbti_main";}

    @GetMapping("/dbti-question")
    public String dogMbtiQuestion() {return "yoonseo/dbti_question";}
}

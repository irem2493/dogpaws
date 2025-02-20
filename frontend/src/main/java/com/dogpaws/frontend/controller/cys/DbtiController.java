package com.dogpaws.frontend.controller.cys;

import com.dogpaws.frontend.dto.ajy.DogDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on 2025-02-19 by 최윤서
 */
@Controller
public class DbtiController {
    @RequestMapping("/dbti-main")
    public String dogMbti() {return "cys/dbti_main";}

    @GetMapping("/dbti-question")
    public String dogMbtiQuestion(HttpSession session, Model model) {
        DogDto dog = (DogDto) session.getAttribute("dog");
        int dogId = dog.getDogId();
        model.addAttribute("dogId", dogId);
        return "cys/dbti_question";
    }

    @GetMapping("/dbti-final")
    public String dbtiFinal() {return "cys/dbti_final";}

}

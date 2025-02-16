package com.dogpaws.frontend.controller.ajy;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {

    @GetMapping("/")
    public String board(Model model) {
        return "/ajy/board/board_register";
    }
}

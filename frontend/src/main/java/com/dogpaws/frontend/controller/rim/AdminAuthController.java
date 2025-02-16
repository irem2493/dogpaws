package com.dogpaws.frontend.controller.rim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/admin/login")
@Controller
public class AdminAuthController {
    @GetMapping
    public String amdinLoginView(Model model) {
        log.info("관리자 페이지 요쳥");
        model.addAttribute("hideSidebar", true);
        return "rim/admin/admin_login.html";
    }
}
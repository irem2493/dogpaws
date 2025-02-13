package com.dogpaws.frontend.controller.rim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AdminAuthController {
    @GetMapping("/admin/login")
    public String amdinLoginPage() {
        log.info("관리자 페이지 요쳥");
        return "rim/admin/admin_login.html";
    }
}

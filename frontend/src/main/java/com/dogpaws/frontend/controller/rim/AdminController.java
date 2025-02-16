package com.dogpaws.frontend.controller.rim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminController {
    @GetMapping("/main")
    public String adminMainView() {
        return "rim/admin/admin_main";
    }
}

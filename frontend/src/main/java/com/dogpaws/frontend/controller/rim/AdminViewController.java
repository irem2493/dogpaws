package com.dogpaws.frontend.controller.rim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminViewController {
    @GetMapping("/main")
    public String adminMainView() {
        return "rim/admin/admin_main";
    }
<<<<<<< HEAD
=======
    @GetMapping("/manage")
    public String productManageView() {
        return "rim/admin/admin_product_manage";
    }
>>>>>>> origin/REQ-68-관리자
}

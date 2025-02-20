package com.dogpaws.frontend.controller.rim;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin/product")
@Controller
public class AdminProductViewController {
    @GetMapping("/regist")
    public String productRegistView() {
        return "rim/admin/admin_product_register";
    }
}
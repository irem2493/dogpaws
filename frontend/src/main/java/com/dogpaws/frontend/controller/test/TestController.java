package com.dogpaws.frontend.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on 2025-01-27 by 구경림
 */
@RequestMapping("/test")
@Controller
public class TestController {

    @GetMapping("/common-css")
    public String commonCss() {
        return "example/common_css";
    }
    @GetMapping("/common-css-code")
    public String commonCssCode() {
        return "example/common_css_code";
    }
    @GetMapping("/modal")
    public String modal() {
        return "example/modal_test";
    }
}

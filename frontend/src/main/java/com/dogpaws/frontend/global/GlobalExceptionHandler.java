package com.dogpaws.frontend.global;


import org.green.frontend.exception.UnauthorizedAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccess(UnauthorizedAccessException ex, RedirectAttributes redirectAttributes) {
        if (ex.getMessage().contains("인증 실패")) {
            redirectAttributes.addFlashAttribute("message", "유효한 토큰 아님 ㅋㅋㅋ");
        } else {
            redirectAttributes.addFlashAttribute("message", "권한 없지롱 ㅋㅋㅋ");
        }
        return "redirect:/login";
    }
}
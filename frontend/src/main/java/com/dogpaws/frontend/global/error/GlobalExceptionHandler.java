package com.dogpaws.frontend.global.error;

import com.dogpaws.frontend.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Created on 2025-02-06 by 구경림
 * 글로벌 예외 처리기
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    //401- 인증실패 (Spring Security가 인증을 거부했을 때(401 Unauthorized))
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ModelAndView handleUnauthorizedAccessException(UnauthorizedAccessException e, HttpServletRequest request) {
        log.error("인증 실패 : {}", request.getRequestURI());
        ModelAndView mav = new ModelAndView("error/401");
        mav.addObject("errorCode", "401");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("path", request.getRequestURI());

        return mav;
    }

    //404
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ModelAndView handleNohandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("페이지를 찾을 수 없음 : {}" , request.getRequestURI());
        ModelAndView mav = new ModelAndView("error/404");

        mav.addObject("errorCode", "404");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    //500 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request) {
        log.error("서버 오류 : {}", request.getRequestURI());
        ModelAndView mav = new ModelAndView("error/500");

        mav.addObject("errorCode", "500");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("path", request.getRequestURI());
        return  mav;
    }

}

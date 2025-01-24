package com.dogpaws.backend.global.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties("error-trace")
@RestControllerAdvice
@Slf4j
public class GlobalException extends ResponseEntityExceptionHandler {

    private boolean stackTrace;

    private List<StackTraceElement> extractStackTrace(Exception ex) {
        return stackTrace ? Arrays.asList(ex.getStackTrace()) : null;
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("에러 ::: [BAD_REQUEST]", ex);
        return ResponseEntity.status(status)
                .body(new ErrorResponse(null, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorResponse handleIOExceptions(Exception ex, WebRequest request) {
        log.error("에러 ::: [IOException]", ex);
        return new ErrorResponse(extractStackTrace(ex), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorResponse handleAllExceptions(Exception ex, WebRequest request) {
        log.error("에러 ::: [AllException]", ex);
        return new ErrorResponse(extractStackTrace(ex), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /*    @ExceptionHandler(DataIntegrityViolationException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public final ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
            log.error("에러 ::: [중복값]", ex);
            String message = "중복된 값이 존재합니다.";
            return new ErrorResponse(extractStackTrace(ex), message, HttpStatus.CONFLICT);
        }*/


    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorResponse handleDatabaseExceptions(DataAccessException ex, WebRequest request) {
        log.error("에러 ::: [DatabaseException]", ex);
        return new ErrorResponse(extractStackTrace(ex), "서버 에러용", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
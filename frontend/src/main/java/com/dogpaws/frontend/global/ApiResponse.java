package com.dogpaws.frontend.global;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private ApiStatus status;
    private T body;
    private LocalDateTime timestamp;
    private Integer httpStatusCode;

    public ApiResponse(ApiStatus status, T body) {
        this.status = status;
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ApiStatus status, T body, Integer httpStatusCode) {
        this.status = status;
        this.body = body;
        this.httpStatusCode = httpStatusCode;
        this.timestamp = LocalDateTime.now();
    }

    public enum ApiStatus {
        SUCCESS,
        ERROR
    }


}

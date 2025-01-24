package com.dogpaws.backend.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ApiStatus status;
    private T body;
    private LocalDateTime timestamp;

    public ApiResponse(ApiStatus status, T body) {
        this.status = status;
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ApiStatus status, T body, boolean includeTimestamp) {
        this.status = status;
        this.body = body;
        if (includeTimestamp) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public enum ApiStatus {
        SUCCESS,
        ERROR
    }
}

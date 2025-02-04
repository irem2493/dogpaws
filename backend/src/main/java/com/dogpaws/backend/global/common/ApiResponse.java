package com.dogpaws.backend.global.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
@JsonDeserialize(using = ApiResponseDeserializer.class)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ApiStatus status;
    private T body;
    private LocalDateTime timestamp;

    // 기본 생성자 추가
    public ApiResponse() {
    }

    @JsonCreator
    public ApiResponse(@JsonProperty("status") ApiStatus status,
                       @JsonProperty("body") T body,
                       @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.status = status;
        this.body = body;
        this.timestamp = timestamp;
    }

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

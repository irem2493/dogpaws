package com.dogpaws.frontend.global;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class ApiResponseDeserializer extends JsonDeserializer<ApiResponse> {

    @Override
    public ApiResponse<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ApiResponse.ApiStatus status = ApiResponse.ApiStatus.valueOf(node.get("status").asText());
        JsonNode bodyNode = node.get("body");

        // timestamp 필드 파싱
        String timestampString = node.has("timestamp") ? node.get("timestamp").asText() : null;

        LocalDateTime timestamp = null;
        if (timestampString != null && !timestampString.isEmpty()) {
            try {
                timestamp = LocalDateTime.parse(timestampString);
            } catch (DateTimeParseException e) {
                // timestamp 파싱 실패 시 예외 처리
                timestamp = null; // 또는 현재 시간 사용 가능: LocalDateTime.now();
            }
        }

        return new ApiResponse<>(status, bodyNode, timestamp);
    }
}
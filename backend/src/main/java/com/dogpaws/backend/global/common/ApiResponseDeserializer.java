package com.dogpaws.backend.global.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public class ApiResponseDeserializer extends JsonDeserializer<ApiResponse> {

    @Override
    public ApiResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ApiResponse.ApiStatus status = ApiResponse.ApiStatus.valueOf(node.get("status").asText());
        JsonNode bodyNode = node.get("body");
        LocalDateTime timestamp = node.has("timestamp") ? LocalDateTime.parse(node.get("timestamp").asText()) : null;

        // body 처리: T에 맞는 타입을 처리하는 로직 추가 필요
        ObjectMapper objectMapper = (ObjectMapper) p.getCodec();
        Object body = objectMapper.treeToValue(bodyNode, Object.class); // 예시로 Object로 처리, 실제 타입에 맞게 수정

        return new ApiResponse<>(status, body, timestamp);
    }
}
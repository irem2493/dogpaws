package com.dogpaws.frontend.dto.ajy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResponseDto {
    private String username; // 사용자 ID (기본키)

    private String provider; // 소셜 로그인 제공자 (ex: google, kakao, naver)

    private String nickname; // 사용자 닉네임

    private String password; // 비밀번호

    private String email; // 이메일 (유니크)

    private String address; // 사용자 주소

    private String postcode; // 사용자 우편주소

    @JsonProperty("detail_address")
    private String detailAddress; // 사용자 상세주소

    @JsonProperty("age_group")
    private String ageGroup; // 연령대 (예: "20대", "30대" 등)

    private String  gender; // 성별 ('M' = 남성, 'F' = 여성)

    private String role;
}

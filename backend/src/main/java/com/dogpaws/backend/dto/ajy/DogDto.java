package com.dogpaws.backend.dto.ajy;

import lombok.Data;

@Data
public class DogDto {
    private int dogId;  // 강아지Id
    private String dogName;
    private String profileUrl;  // 강아지 프로필 이미지 URLs
}

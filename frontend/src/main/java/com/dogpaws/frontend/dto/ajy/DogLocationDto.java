package com.dogpaws.frontend.dto.ajy;

import lombok.Data;

@Data
public class DogLocationDto {

    private Integer dogId;
    private String username;
    private String dogName;
    private String profileUrl;
    private double latitude;  // 위도
    private double longitude; // 경도
}
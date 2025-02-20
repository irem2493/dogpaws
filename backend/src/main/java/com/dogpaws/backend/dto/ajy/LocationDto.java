package com.dogpaws.backend.dto.ajy;

import lombok.Data;

@Data
public class LocationDto {
    private double latitude;  // 위도
    private double longitude; // 경도
}

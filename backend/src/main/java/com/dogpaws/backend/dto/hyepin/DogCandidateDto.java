package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.time.LocalTime;

@Data
public class DogCandidateDto {

    private int dogId;
    private String username;
    private String breed;
    private String isMix;
    private double weight;
    private String personalityType;
    private LocalTime walkStartTime;
    private LocalTime walkEndTime;
    private String walkDays;
    private String address;
    private String dogRegion;
    private String userRegion; // DB에서 받아온 사용자 지역 코드 (비교용)

    // 후처리 결과
    private int matchScore;
    private String matchedCriteria;

}

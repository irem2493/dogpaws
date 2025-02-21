package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.util.List;

@Data
public class LikeDogDto {

    private int likeId; //좋아요 번호 좋아요좋아요
    private int dogId; // 상대 dog ID
    private String username;  // 사용자 ID (외래키)
    private String dogName;  // 강아지 이름
    private String breed;  // 품종
    private String isMix;  // 믹스 여부 (Y/N)
    private String birthYear;  // 생년
    private String birthMonth;  // 월
    private String gender;  // 성별 (M/F)
    private String isNeutered;  // 중성화 여부 (Y/N)
    private String profileUrl;  // 강아지 프로필 이미지 URL

    private boolean liked; //좋아요 여부

    private String dogPersonalGbnCds;
    private String dogPlayGbnCds;

    //리스트
    private List<String> dogPersonalGbnCdsList;
    private List<String> dogPlayGbnCdsList;
}

package com.dogpaws.backend.dto.ajy;

import com.dogpaws.backend.entity.File;
import lombok.Data;

@Data
public class DogResponseDto {
    private String username;  // 사용자 ID (외래키)

    private Integer dogId;

    private String dogName;  // 강아지 이름

    private String breed;  // 품종

    private String isMix;  // 믹스 여부 (Y/N)

    private String birthYear;  // 생년

    private String birthMonth;  // 월

    private String gender;  // 성별 (M/F)

    private String isNeutered;  // 중성화 여부 (Y/N)

    private Integer weight;  // 체중

    private String walkStartTime;  // 산책 시작 시간

    private String walkEndTime;  // 산책 종료 시간

    private String walkTimeYn;  // 산책 시간 선택 여부 (Y/N)

    private String walkDays;  // 산책 요일 (월, 화, 수, 목, 금, 토, 일)

    private String isMatingAvailable;  // 교배 매칭 여부 (Y/N)

    private String dogIntro;  // 강아지 소개

    private String personalityType;  // 성격 유형

    private String profileUrl;  // 강아지 프로필 이미지 URL

    private String selectedPersonalities;

    private String selectedPlays;

    private File peFile;    //혈통증명서
    private File vaFile;    //예방접종증명서
    private File heFile;    //건강검진증명서
<<<<<<< HEAD

    private boolean friendLiked; //친구 좋아요
    private boolean matingLiked; //교배 좋아요
=======
>>>>>>> origin/REQ-68-관리자
}

package com.dogpaws.frontend.dto.hyepin;
import lombok.Data;

import java.util.List;

@Data
public class MatchDto {
    private int dogId;
    private String username;  // 사용자 ID (외래키)
    private String dogName;  // 강아지 이름
    private String breed;  // 품종
    private String breedName;  // 품종 (한글)
    private String address;  // 품종 (한글)
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
    private int matchScore; // 매칭 점수
    private String matchedCriteria; // 매칭 일치조건 , 형식 문자열
    private List<String> matchedCriteriaList; //매칭 일치조건 리스트

}

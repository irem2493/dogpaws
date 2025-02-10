package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class FilterDto {
    private Long dogMatchId;  // 매칭 필터 고유 넘버
    private Integer dogId;  // 강아지 ID
    private String breedGbnCd;  // 견종 구분 코드
    private char isMix;  // 믹스여부
    private Integer weight;  // 체중
    private char weightCategory;  // 체중 구분 (이상/이하)
    private String dogTypeCodeGbnCd;  // 견BTI 코드
    private LocalTime walkStartTime;  // 산책 시작 시간
    private LocalTime walkEndTime;  // 산책 종료 시간
    private String walkDays;  // 산책 요일
    private char bloodTestCertified;  // 혈통서 증명서 여부 (Y/N)
    private char vaccinationCertified;  // 예방접종 증명서 여부 (Y/N)
    private char healthRecordCertified;  // 건강기록 증명서 여부 (Y/N)
    private char matchType;  // 매칭 구분 (친구/교배)

    //보여지는 시간 string
    private String strWalkStartTime;  // 산책 시작 시간
    private String strWalkEndTime;  // 산책 종료 시간

    // 구분 테이블 조인
    private String breed;  // 견종 한글
    private String dogType;  // 견BTI 한글

    // concat
    private String dogPersonal;  // 성격 1~5 한글
    private String dogPlay;  // 놀이 1~5 한글
    private String dogPersonalGbnCd;  // 성격 1~5 코드
    private String dogPlayGbnCd;  // 놀이 1~5 코드

    //리스트 변환 작업 필요
    private List<String> dogPersonalList;  // 성격 1~5 한글 리스트
    private List<String> dogPlayList;  // 놀이 1~5 한글 리스트
    private List<String> dogPersonalGbnCdList;  // 성격 1~5 코드 리스트
    private List<String> dogPlayGbnCdList;  // 놀이 1~5 코드 리스트
    private List<String> walkDayList; // 산책 요일 리스트
}

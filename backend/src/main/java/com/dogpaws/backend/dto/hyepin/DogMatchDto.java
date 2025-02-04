package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class DogMatchDto {
    private Long dogMatchId;  // 매칭 필터 고유 넘버
    private Integer dogId;  // 강아지 ID
    private String breedGbnCd;  // 견종 구분 코드
    private String breed;  // 견종
    private Integer weight;  // 체중
    private Character weightCategory;  // 체중 구분 (이상/이하)
    private String dogTypeCodeGbnCd;  // 견BTI 코드
    private String dogType;  // 견BTI
    private List<String> dogPersonalGbnCd;  // 성격 1~5
    private List<String> dogPlayGbnCd;  // 놀이 1~5
    private LocalTime walkStartTime;  // 산책 시작 시간
    private LocalTime walkEndTime;  // 산책 종료 시간
    private String walkDays;  // 산책 요일
    private Character bloodTestCertified;  // 혈통서 증명서 여부 (Y/N)
    private Character vaccinationCertified;  // 예방접종 증명서 여부 (Y/N)
    private Character healthRecordCertified;  // 건강기록 증명서 여부 (Y/N)
    private Character matchType;  // 매칭 구분 (친구/교배)

}

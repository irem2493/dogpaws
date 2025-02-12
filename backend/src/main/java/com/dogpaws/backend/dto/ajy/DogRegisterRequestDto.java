package com.dogpaws.backend.dto.ajy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DogRegisterRequestDto {
    private String username;  // 사용자 ID (외래키)

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

    private String fileOldName;  // 원본 파일 이름

    private String fileNewName;  // 저장된 파일 이름

    private String fileExt;  // 파일 확장자

    private Long fileSize;  //파일 크기

    @JsonIgnore
    private MultipartFile profileImage;

    private String selectedPersonalities;

    private String selectedPlays;

    // 활동 사진 리스트 (최대 6개)
    @JsonIgnore
    private List<MultipartFile> activityImages;  // 활동 사진 리스트

    // 활동 사진 메타데이터 리스트
    private List<String> activityImageMetadata;  // 각 사진의 정보

    private String activityImageFileName;

    private List<MultipartFile> matchingSelectFileList;
}

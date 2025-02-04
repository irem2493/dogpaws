package com.dogpaws.backend.entity.ajy;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_dogs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_id")
    private Integer dogId;  // 강아지 ID (기본키, 자동증가)

    @Column(name = "username", nullable = false)
    private String username;  // 사용자 ID (외래키)

    @Column(name = "dog_name", nullable = false)
    private String dogName;  // 강아지 이름

    @Column(name = "breed", nullable = false)
    private String breed;  // 품종

    @Column(name = "is_mix", length = 1, nullable = true)
    private String isMix;  // 믹스 여부 (Y/N)

    @Column(name = "birth_year", nullable = true)
    private String birthYear;  // 생년

    @Column(name = "birth_month", nullable = true)
    private String birthMonth;  // 월

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;  // 성별 (M/F)

    @Column(name = "is_neutered", length = 1, nullable = true)
    private String isNeutered;  // 중성화 여부 (Y/N)

    @Column(name = "weight", nullable = true)
    private Integer weight;  // 체중

    @Column(name = "walk_start_time", nullable = true)
    private String walkStartTime;  // 산책 시작 시간

    @Column(name = "walk_end_time", nullable = true)
    private String walkEndTime;  // 산책 종료 시간

    @Column(name = "walk_time_yn", length = 1, nullable = true)
    private String walkTimeYn;  // 산책 시간 선택 여부 (Y/N)

    @Column(name = "walk_days", nullable = true)
    private String walkDays;  // 산책 요일 (월, 화, 수, 목, 금, 토, 일)

    @Column(name = "is_mating_available", length = 1, nullable = true)
    private String isMatingAvailable;  // 교배 매칭 여부 (Y/N)

    @Column(name = "dog_intro", nullable = true)
    private String dogIntro;  // 강아지 소개

    @Column(name = "personality_type", nullable = true)
    private String personalityType;  // 성격 유형

    @Column(name = "profile_url", nullable = true)
    private String profileUrl;  // 강아지 프로필 이미지 URL

    @Column(name = "file_old_name", nullable = true)
    private String fileOldName;  // 원본 파일 이름

    @Column(name = "file_new_name", nullable = true)
    private String fileNewName;  // 저장된 파일 이름

    @Column(name = "file_ext", nullable = true)
    private String fileExt;  // 파일 확장자

    @Column(name = "file_size", nullable = true)
    private Long fileSize;  //파일 크기


}

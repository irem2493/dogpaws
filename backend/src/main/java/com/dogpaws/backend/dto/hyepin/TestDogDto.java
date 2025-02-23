package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.util.List;

@Data
public class TestDogDto {
    private int dogId;
    private String dogName;
    private String username;
    private String breed;
    private String isMix;
    private String birthYear;
    private String birthMonth;
    private String gender;
    private String isNeutered;
    private int weight;
    private String walkStartTime;
    private String walkEndTime;
    private String walkDays;
    private String isMatingAvailable;
    // dog_intro 생략 (혹은 별도 활용)

    // 성격 리스트를 List<String>으로 관리 (매퍼에서 각 항목을 개별 컬럼에 매핑 가능)
    private List<String> personalityTypes;

    // 놀이 리스트를 List<String>으로 관리
    private List<String> playList;

    private String profileUrl;
    private String bloodTestCertified;
    private String vaccinationCertified;
    private String healthRecordCertified;
}

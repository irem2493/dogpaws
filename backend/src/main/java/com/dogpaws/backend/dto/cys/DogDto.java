package com.dogpaws.backend.dto.cys;

import lombok.Data;

/**
 * Created on 2025-02-19 by 최윤서
 */
@Data
public class DogDto {

    private int dogId;
    private String dogName;
    private String breed;
    private char isMix;
    private String dogIntro;
    private String personalityType;
    private String profileUrl;

}

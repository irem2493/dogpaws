package com.dogpaws.backend.dto.cys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2025-02-12 by 최윤서
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DogResponseDto {


    private int dogId;  // 강아지Id

    private String dogName;  // 강아지 이름

    private String profileUrl;  // 강아지 프로필 이미지 URL

    private String username;    //보호자 id

    private String nickname;    //보호자 닉네임
}

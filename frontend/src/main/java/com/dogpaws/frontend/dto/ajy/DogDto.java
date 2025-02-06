package com.dogpaws.frontend.dto.ajy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DogDto {

    private String dogName;  // 강아지 이름

    private String profileUrl;  // 강아지 프로필 이미지 URL

}

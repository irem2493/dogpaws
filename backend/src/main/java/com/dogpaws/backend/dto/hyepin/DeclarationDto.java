package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

@Data
public class DeclarationDto {

    private String username; //신고자
    private int dogId; //신고받을 강아지ID
    private String declarationCode; //신고 사유 코드
    private String declarationContent; // 신고 사유 기입


}

package com.dogpaws.backend.dto.common;

import lombok.Data;


@Data
public class GubnDto {
    private String groupCode;  // GROUP_CODE (예: user_role, question, graduate)
    private String gubnCode;   // 구분 코드
    private String gubnName;   // 구분명
}

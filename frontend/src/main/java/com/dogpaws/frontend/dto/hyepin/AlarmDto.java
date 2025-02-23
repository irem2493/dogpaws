package com.dogpaws.frontend.dto.hyepin;

import lombok.Data;
<<<<<<< HEAD
import lombok.ToString;

@Data
@ToString
=======

@Data
>>>>>>> origin/REQ-68-관리자
public class AlarmDto {

    private int alarmId;
    private String username; //사용자 ID (외래키)
    private String alarmType; //알람 유형
    private String gubnId; //알림과 관련된 ID (구체적인 알림 대상에 따라 달라짐)
    private String message; //알림 메시지 내용
    private String readStatus; //읽음여부
<<<<<<< HEAD
    private String createdAt;//알림생성시각
    private int dogId; // 강아지 ID
=======
>>>>>>> origin/REQ-68-관리자

}

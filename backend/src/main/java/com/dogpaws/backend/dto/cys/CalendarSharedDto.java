package com.dogpaws.backend.dto.cys;

import lombok.Data;

/**
 * Created on 2025-02-14 by 최윤서
 */
@Data
public class CalendarSharedDto {

    private int sharedId;   //공유 키 (기본키)
    private int calendarId; //일정 id
    private String username;    //공유받은 사람 id
    private String roomId;  //채팅방 id
    private String dogId;   //강아지 id

}

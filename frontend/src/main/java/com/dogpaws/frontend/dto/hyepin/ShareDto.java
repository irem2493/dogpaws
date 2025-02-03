package com.dogpaws.frontend.dto.hyepin;

import lombok.Data;

@Data
public class ShareDto {
    private Integer sharedId;   // 일정 공유 고유 ID
    private Integer calendarId; // 일정 ID
    private String username;    // 공유받은 사용자 ID (dto 사용시 공유한 사람, 공유 받은 사람 둘 다 쓸 수 있음)
    private Integer roomId;     // 채팅방 번호 (nullable)
    private Integer dogId;      // 반려견 ID
}

package com.dogpaws.backend.dto.rim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationScheduleDto {
    private Long id;
    private String username;
    private Long calendarId;
    private String calendarTitle;
    private LocalDateTime scheduleTime;
    private String status;
    private String alarmType;    // 알림 유형 (C: 캘린더)
    private String gubnId;      // 구분 ID
    private String message;     // 알림 메시지
}
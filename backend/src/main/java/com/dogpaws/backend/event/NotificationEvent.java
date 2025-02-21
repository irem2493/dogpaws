package com.dogpaws.backend.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class NotificationEvent {
    private final String alarmType;    // CHAT, CALENDAR, LIKE, ADMIN, MATCHING
    private final String username;     // 수신자 (null이면 전체발송)
    private final String title;        // 알림 제목
    private final String message;      // 알림 내용
    private final String gubnId;       // 구분 ID 추가

    public static NotificationEvent calendarReminder(String username, String message) {
        return new NotificationEvent(
                "C",  // Calendar type
                username,
                "내일 일정 알림",
                 message,
                "C"
        );
    }

}
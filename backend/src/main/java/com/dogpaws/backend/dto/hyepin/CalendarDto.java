package com.dogpaws.backend.dto.hyepin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarDto {

    private int calendarId; // 일정 ID (기본키)
    private String username; // 사용자 ID (tbl_users와 연결된 외래키)
    private int dogId;   // 강아지 ID (tbl_dogs와 연결된 외래키)
    private String calendarTitle;  // 일정 제목
    private String address;  // 장소 (NULL 가능)
    private char calendarType; // 일정 종류 (산책: W, 놀이: P, 교배: M)
    private char isAllDay;  // 하루 종일 여부 (Y: 하루 종일, N: 특정 시간대)
    private String calendarStartDate; // 일정 시작 날짜/시간 (하루 종일일 경우 00시로 설정)
    private String calendarEndDate;  // 일정 종료 날짜/시간 (하루 종일일 경우 23:59로 설정)
    private String calendarDescription;  // 일정 설명 (NULL 가능)
    private LocalDateTime createdAt;   // 일정 생성일 (현재 시간으로 기본 설정)
    private LocalDateTime updatedAt;  // 일정 수정일 (현재 시간으로 기본 설정)
    private Integer roomId; // 채팅방 번호 (NULL 가능)

}
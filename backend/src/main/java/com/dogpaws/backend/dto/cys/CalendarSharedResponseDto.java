package com.dogpaws.backend.dto.cys;

import lombok.Data;

/**
 * Created on 2025-02-18 by 최윤서
 */
@Data
public class CalendarSharedResponseDto {

    private int calendarId;
    private String roomId;
    private String calendarTitle;
    private String calendarStartDate;
    private String calendarEndDate;

}

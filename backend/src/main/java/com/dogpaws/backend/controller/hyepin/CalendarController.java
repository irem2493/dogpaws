package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.hyepin.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ApiResponse<String> registCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 registCalendar / calendarDto 값: {}", calendarDto);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 저장 성공");
    }

    @PutMapping
    public ApiResponse<String> updateCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 updateCalendar / calendarDto 값: {}", calendarDto);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수정 성공");
    }

    @DeleteMapping
    public ApiResponse<String> deleteCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 deleteCalendar / calendarDto 값: {}", calendarDto);

        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 삭제 성공");
    }
    
}
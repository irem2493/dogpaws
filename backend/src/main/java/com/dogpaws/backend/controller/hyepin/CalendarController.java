package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.hyepin.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ApiResponse<String> registCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 registCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.insertCalendar(calendarDto);
        if(result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 저장 성공");
        }else{
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 저장 실패");
        }
    }

    @PutMapping
    public ApiResponse<String> updateCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 updateCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.updateCalendar(calendarDto);
        if(result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수정 성공");
        }else{
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수정 실패");
        }
    }

    @PostMapping("/delete")
    public ApiResponse<String> deleteCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 deleteCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.deleteCalendar(calendarDto);
        if(result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 삭제 성공");
        }else{
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 삭제 실패");
        }
    }

    @GetMapping
    public ApiResponse<List<CalendarDto>> getCalendar(@RequestParam String username) throws IOException {
        List<CalendarDto> calendarList = calendarService.getCalendarByUsername(username);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, calendarList);
    }
    
}
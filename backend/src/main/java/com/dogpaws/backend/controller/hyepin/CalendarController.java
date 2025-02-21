package com.dogpaws.backend.controller.hyepin;

import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.dto.hyepin.ShareDto;
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

    //일정 등록
    @PostMapping
    public ApiResponse<String> registCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 registCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.insertCalendar(calendarDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 저장 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 저장 실패");
        }
    }

    //일정 수정
    @PutMapping
    public ApiResponse<String> updateCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 updateCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.updateCalendar(calendarDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수정 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수정 실패");
        }
    }

    //일정 삭제
    @PostMapping("/delete")
    public ApiResponse<String> deleteCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        log.info("여기는 백 컨트롤러 deleteCalendar / calendarDto 값: {}", calendarDto);
        int result = calendarService.deleteCalendar(calendarDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 삭제 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 삭제 실패");
        }
    }

    //일정 리스트
    @GetMapping
    public ApiResponse<List<CalendarDto>> getCalendar(@RequestParam String username) throws IOException {
        List<CalendarDto> calendarList = calendarService.getCalendarByUsername(username);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, calendarList);
    }

    //일정 공유
    @PostMapping("share")
    public ApiResponse<String> shareCalendar(@ModelAttribute CalendarDto calendarDto) throws IOException {
        //채팅방으로 연결 -> 채팅방에 일정이 공유됨.
        //상대방 알림 테이블에 등록(알림유형 - C / 구분코드 - SH(calendar_code)
        //일정 테이블에 공유 현황 업데이트
        log.info("shareCalendar / calendarDto 값: {}", calendarDto);
        System.out.println("calendarDto: " + calendarDto);
        int result = calendarService.shareCalendar(calendarDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 공유 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 공유 실패");
        }

    }

    //일정 공유받기
    @PostMapping("shared")
    public ApiResponse<String> sharedCalendar(@ModelAttribute ShareDto shareDto) throws IOException {
        //로그인값 받아오기
        String username = "안혜빈";
        shareDto.setUsername(username);
        //공유 일정 테이블에 저장
        calendarService.insertSharedCalendar(shareDto);
        return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 수락 성공");
    }

}
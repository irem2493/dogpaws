package com.dogpaws.backend.controller.cys;

import com.dogpaws.backend.dto.cys.DogResponseDto;
import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.global.common.ApiResponse;
import com.dogpaws.backend.service.cys.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2025-02-11 by 최윤서
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/profile")
    public List<DogResponseDto> profile(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> otherParticipants = requestData.get("otherParticipants");
        System.out.println("강아지 id 리스트 : "+otherParticipants);
        List<DogResponseDto> profiles = chatService.getChatProfile(otherParticipants);
        System.out.println("강아지 정보 리스트 : "+profiles);
        return profiles;
    }

    @PostMapping("/schedule")
    public ApiResponse<String> registSchedule(@ModelAttribute CalendarDto calendarDto) throws IOException {
        int result = chatService.insertSchedule(calendarDto);
        if (result == 1) {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 등록 성공");
        } else {
            return new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, "일정 등록 실패");
        }
    }

}

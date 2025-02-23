package com.dogpaws.backend.controller.cys;

import com.dogpaws.backend.dto.cys.CalendarSharedDto;
import com.dogpaws.backend.dto.cys.CalendarSharedResponseDto;
import com.dogpaws.backend.dto.cys.DogResponseDto;
import com.dogpaws.backend.dto.cys.RatingResponseDto;
import com.dogpaws.backend.dto.hyepin.CalendarDto;
import com.dogpaws.backend.service.common.FileService;
import com.dogpaws.backend.service.cys.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private FileService fileService;

    @PostMapping("/profile")
    public List<DogResponseDto> profile(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> otherParticipants = requestData.get("otherParticipants");
        System.out.println("강아지 id 리스트 : "+otherParticipants);
        List<DogResponseDto> profiles = chatService.getChatProfile(otherParticipants);
        System.out.println("강아지 정보 리스트 : "+profiles);
        return profiles;
    }

    @PostMapping("/schedule")
    public int registSchedule(@ModelAttribute CalendarDto calendarDto) throws IOException {
        int calendarId = chatService.insertSchedule(calendarDto);
        System.out.println("컨트롤러 id : "+calendarId);
        return calendarId;
    }

    @GetMapping("/schedule")
    public CalendarDto getSchedule(@RequestParam("calendarId") int calendarId) throws IOException {
        CalendarDto dto = chatService.getSchedule(calendarId);
        System.out.println("받은 id : "+calendarId);
        return dto;
    }

    @PostMapping("/shared-schedule")
    public void sharedSchedule(@RequestBody Map<String, String> requestData) throws IOException {
        String calendarId = requestData.get("calendarId");
        String username = requestData.get("username");
        String roomId = requestData.get("roomId");
        String dogId = requestData.get("dogId");

        CalendarSharedDto sharedDto = new CalendarSharedDto();
        sharedDto.setCalendarId(Integer.parseInt(calendarId));
        sharedDto.setRoomId(roomId);
        sharedDto.setDogId(dogId);
        sharedDto.setUsername(username);

        chatService.sharedSchedule(sharedDto);
    }

    @PostMapping("/fileUpload")
    public List<String> fileUpload(@RequestParam("files") MultipartFile[] files
                                        , @RequestParam("dogId") String dogId
                                        , @RequestParam("roomId") String roomId) throws IOException {
        System.out.println(Arrays.toString(files));
        System.out.println(dogId);
        System.out.println(roomId);
        List<String> fileUrlList = new ArrayList<>();

        for(MultipartFile file : files){
            String fileUrl = fileService.saveChatFile(file, "CH", roomId, dogId);
            fileUrlList.add(fileUrl);
            System.out.println(fileUrl);
        }
        return fileUrlList;
    }

    @GetMapping("/mediaListAll")
    public List<String> getAllMediaUrl(@RequestParam("roomId") String roomId) throws IOException {
        return chatService.getAllMediaUrl(roomId);
    }

    @GetMapping("/sharedCalendar")
    public List<CalendarSharedResponseDto> getSharedCalendar(@RequestParam("roomId") String roomId) throws IOException {
        return chatService.getSharedCalendar(roomId);
    }

    @PostMapping("/star")
    public void registStr(@RequestBody Map<String, String> requestData) throws IOException {
        String userStar = requestData.get("user_star");
        String reviewerId = requestData.get("reviewer_id");
        String recipientId = requestData.get("recipient_id");
        chatService.registStr(userStar, reviewerId, recipientId);
    }

    @GetMapping("/star")
    public RatingResponseDto getCntStrById(@RequestParam("reviewer_id") String reviewerId,
                                           @RequestParam("recipient_id") String recipientId) throws IOException {

        int strCnt = chatService.getCntStrById(reviewerId, recipientId);
        double rating = chatService.getStrById(reviewerId, recipientId);

        RatingResponseDto dto = new RatingResponseDto();
        dto.setStrCnt(strCnt);
        dto.setRating(rating);

        return dto;
    }

}

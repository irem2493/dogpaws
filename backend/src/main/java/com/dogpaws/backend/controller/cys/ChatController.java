package com.dogpaws.backend.controller.cys;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.service.cys.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 2025-02-11 by 최윤서
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/chat/profile")
    public List<DogDto> profile(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> otherParticipants = requestData.get("otherParticipants");
        System.out.println("강아지 id 리스트 : "+otherParticipants);
        List<DogDto> profiles = chatService.getChatProfile(otherParticipants);
        System.out.println("강아지 정보 리스트 : "+profiles);
        return profiles;
    }

}

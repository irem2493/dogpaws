package com.dogpaws.frontend.controller.cys;

import com.dogpaws.frontend.dto.ajy.DogDto;
import com.dogpaws.frontend.dto.ajy.UserDto;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on 2025-02-05 by 최윤서
 */
@Controller
public class ChatController {

    @GetMapping("/chat-room")
    public String chatRoom(HttpSession session, Model model, @RequestParam(value = "id", required = false) String inviteId) {
        DogDto dog = (DogDto) session.getAttribute("dog");
        UserDto user = (UserDto) session.getAttribute("user");
        int dogId = dog.getDogId();
        String dogName = dog.getDogName();
        String dogProfile = dog.getProfileUrl();
        model.addAttribute("dogId", dogId);
        model.addAttribute("dogProfile", dogProfile);

        String username = user.getUsername();
        String nickname = user.getNickname();
        model.addAttribute("inviteId", inviteId);
        model.addAttribute("username", username);
        model.addAttribute("nickname", nickname);
        model.addAttribute("dogName", dogName);
        System.out.println(dogId+':'+username+':'+nickname+':'+dogProfile);
        return "cys/chat_room";
    }

    @GetMapping("/chat-invite")
    public String chatInvite(@RequestParam(value = "id", required = false) String inviteId, Model model) {

        model.addAttribute("inviteId", inviteId);
        return "redirect:/chat-room?id="+inviteId;
    }
}

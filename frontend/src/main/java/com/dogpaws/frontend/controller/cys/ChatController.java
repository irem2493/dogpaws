package com.dogpaws.frontend.controller.cys;

import com.dogpaws.frontend.dto.ajy.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on 2025-02-05 by 최윤서
 */
@Controller
public class ChatController {

    @GetMapping("/chat-room")
    public String chatRoom(HttpSession session, Model model) {
        UserDto user = (UserDto) session.getAttribute("user");
        String username = user.getUsername();
        model.addAttribute("username", username);
        return "cys/chat_room";
    }
}

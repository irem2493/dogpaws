package com.dogpaws.frontend.controller.cys;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created on 2025-02-05 by 최윤서
 */
@Controller
public class ChatController {

    @RequestMapping("/chat-room")
    public String chatRoom() {
        return "cys/chat_room";
    }
}

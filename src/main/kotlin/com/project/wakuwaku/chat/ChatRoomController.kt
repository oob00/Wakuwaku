package com.project.wakuwaku.chat

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ChatRoomController {

    @GetMapping("/chat")
    fun chatRoom(): String {
        return "chatroom" // resources/templates/chatroom.html에 해당하는 뷰 이름 반환
    }
}
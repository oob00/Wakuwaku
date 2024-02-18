package com.project.wakuwaku.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.wakuwaku.model.redis.chatroom.Chatroom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer


@Controller
@RequestMapping("/chat")
class ChatRoomController(
        private val chatRoomService: ChatRoomService
) {

    @GetMapping("/")
    fun chatRoom(): String {
        return "chatroom" // resources/templates/chatroom.html에 해당하는 뷰 이름 반환
    }

    @GetMapping("/rooms")
    @ResponseBody
    fun room(): List<Chatroom> {
        val chatRooms: List<Chatroom> = chatRoomService.findAllRoom()
        chatRooms.forEach { room ->
            room.userCount = chatRoomService.getUserCount(room.roomId)
        }
        return chatRooms
    }

    @PostMapping("/room")
    @ResponseBody
    fun createRoom(@RequestParam name: String): Chatroom {
        return chatRoomService.createChatRoom(name)
    }

    @GetMapping("/room/enter/{roomId}")
    fun roomDetail(model: Model, @PathVariable roomId: String): String {
        model.addAttribute("roomId", roomId)

        val chatroom: Chatroom? = chatRoomService.findRoomById(roomId)
        val userCount: Long = chatRoomService.getUserCount(roomId)
        if (chatroom != null) {
            model.addAttribute("roomName", chatroom.roomName)
            model.addAttribute("userCount", userCount)
        }
        return "chatroomDetail"
    }

    @PostMapping("/room/userCount")
    @ResponseBody
    fun roomUserCount(@RequestParam roomId: String): Long {
        val userCount: Long = chatRoomService.getUserCount(roomId)
        return userCount
    }
}
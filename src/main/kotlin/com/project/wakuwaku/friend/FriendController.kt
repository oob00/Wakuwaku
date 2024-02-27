package com.project.wakuwaku.friend

import com.project.wakuwaku.friend.dto.FriendDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/friend")
@RestController
class FriendController(
    private val friendService: FriendService
) {

    @PostMapping("/list")
    fun getFriendList(@RequestBody id: String): ResponseEntity<List<FriendDto>> {
        val friendList = friendService.getFriendList(id)

        return ResponseEntity.ok(friendList)
    }

    @PostMapping("/request")
    fun requestFriend(@RequestBody id: String, friendId: String): ResponseEntity<String> {
        val result = friendService.requestFriend(id, friendId)

        return ResponseEntity.ok(if (result) "success" else "fail")
    }

    @PostMapping("/received/list")
    fun getFriendRequestReceived(@RequestBody id: String): ResponseEntity<List<FriendDto>> {
        val receivedList = friendService.getFriendRequestReceived(id)

        return ResponseEntity.ok(receivedList)
    }

    @PostMapping("/accept")
    fun acceptFriend(@RequestBody id: String, friendId: String): ResponseEntity<String> {
        val result = friendService.acceptFriend(id, friendId)

        return ResponseEntity.ok(if (result) "success" else "fail")
    }
}
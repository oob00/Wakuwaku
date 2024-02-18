package com.project.wakuwaku.model.redis.chatroom

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class Chatroom (
        var roomName: String,
) : Serializable {

        var roomId: String = UUID.randomUUID().toString()
        var userCount: Long = 0
        var createDt: LocalDateTime = LocalDateTime.now()
}
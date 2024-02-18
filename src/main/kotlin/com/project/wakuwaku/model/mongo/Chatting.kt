package com.project.wakuwaku.model.mongo

import com.project.wakuwaku.model.kafka.KafkaMessageDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.time.ZoneId

@Document(collection = "chatting")
data class Chatting(
        @Id
        val id: String? = null,
        val chatRoomId: String? = null,
        var senderId: String? = null,
        var senderName: String? = null,
        val contentType: String? = null,
        val content: String? = null,
        val sendDate: LocalDateTime? = null,
        var readCount: Int = 0
) {
        fun convertKafkaMsg(): KafkaMessageDto = KafkaMessageDto(
                id = id,
                chatRoomId = chatRoomId,
                senderId = senderId,
                senderName = senderName,
                contentType = contentType,
                content = content,
                sendTime = sendDate?.atZone(ZoneId.of("Asia/Seoul"))?.toInstant()?.toEpochMilli() ?: 0L,
                readCount = readCount
        )

        fun setSenderInfo(authentication: Authentication){
                senderId = authentication.name
                senderName = authentication.name
        }
}
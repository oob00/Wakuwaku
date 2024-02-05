package com.project.wakuwaku.model.kafka

import com.project.wakuwaku.model.mongo.Chatting
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class KafkaMessageDto(
        var id: String? = null,

        @field:NotNull
        var chatNo: Int? = null,

        @field:NotNull
        var contentType: String? = null,

        @field:NotNull
        var content: String? = null,

        var senderName: String? = null,
        var senderNo: Int? = null,

        @field:NotNull
        var saleNo: Int? = null,

        var sendTime: Long = 0,
        var readCount: Int = 0,
        var senderEmail: String? = null
) : Serializable {

    fun setSendTimeAndSender(sendTime: LocalDateTime, senderNo: Int, senderName: String, readCount: Int) {
        this.senderName = senderName
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
        this.senderNo = senderNo
        this.readCount = readCount
    }

    fun convertEntity(): Chatting = Chatting(
            id = id,
            chatRoomNo = chatNo,
            senderNo = senderNo,
            senderName = senderName,
            contentType = contentType,
            content = content,
            sendDate = Instant.ofEpochMilli(sendTime).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime(),
            readCount = readCount
    )
}
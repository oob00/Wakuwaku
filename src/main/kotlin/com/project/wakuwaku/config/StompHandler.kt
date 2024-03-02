package com.project.wakuwaku.config

import com.project.wakuwaku.chat.ChatRoomService
import com.project.wakuwaku.chat.ChatService
import com.project.wakuwaku.config.kafka.KafkaConstants
import com.project.wakuwaku.model.jpa.user.UserRepository
import com.project.wakuwaku.model.jpa.user.Users
import com.project.wakuwaku.model.kafka.KafkaMessageDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import java.security.Principal
import java.util.*
import kotlin.jvm.optionals.getOrNull


@Component
class StompHandler @Autowired constructor (
        private val chatroomService : ChatRoomService,
        var kafkaTemplate: KafkaTemplate<String, KafkaMessageDto>,
        private val userRepository: UserRepository
) : ChannelInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = StompHeaderAccessor.wrap(message)
        if (StompCommand.CONNECT == accessor.command) { // websocket 연결요청
            val jwtToken = accessor.getFirstNativeHeader("token")
            // Header의 jwt token 검증
            //jwtTokenProvider.validateToken(jwtToken)
        } else if (StompCommand.SUBSCRIBE == accessor.command) { // 채팅룸 구독요청
            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            val roomId: String = chatroomService.getRoomId(Optional.ofNullable(message.headers["simpDestination"].toString()).orElse("InvalidRoomId"))
            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            val sessionId: String = message.headers["simpSessionId"].toString()
            chatroomService.setUserEnterInfo(sessionId, roomId)
            // 채팅방의 인원수를 +1한다.
            chatroomService.plusUserCount(roomId)
            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            val id: String = Optional.ofNullable(message.headers["simpUser"] as Principal?).map { obj: Principal -> obj.name }.orElse("UnknownUser")

            val user: Users = userRepository.findById(id).get()

            println("println id: $id, nickName: ${user.nickname}")

            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC,KafkaMessageDto(chatRoomId = roomId,senderId = "SYSTEM", senderName = "알림", content = user.nickname+" 님이 접속하였습니다."))
            log.info("SUBSCRIBED {}, {}", sessionId, roomId)
        } else if (StompCommand.DISCONNECT == accessor.command) { // Websocket 연결 종료

            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            val sessionId = message.headers["simpSessionId"] as String
            val roomId: String = chatroomService.getUserEnterRoomId(sessionId)

            // 채팅방의 인원수를 -1한다.
            chatroomService.minusUserCount(roomId)

            // 클라이언트 퇴장 메시지를 채팅방에 발송한다.(redis publish)
            val id = Optional.ofNullable(message.headers["simpUser"] as Principal?).map { obj: Principal -> obj.name }.orElse("UnknownUser")

            val user: Users = userRepository.findById(id).get()

            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC,KafkaMessageDto(chatRoomId = roomId,senderId = "SYSTEM", senderName = "알림", content = user.nickname+" 님이 퇴장하였습니다."))

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatroomService.removeUserEnterInfo(sessionId)
            log.info("DISCONNECTED {}, {}", sessionId)
        }
        return message
    }
}
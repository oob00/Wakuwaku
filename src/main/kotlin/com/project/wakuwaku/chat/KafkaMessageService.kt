package com.project.wakuwaku.chat

import com.project.wakuwaku.config.kafka.KafkaConstants
import com.project.wakuwaku.model.kafka.KafkaMessageDto
import com.project.wakuwaku.model.mongo.Chatting
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class KafkaMessageService @Autowired constructor(
        var kafkaTemplate: KafkaTemplate<String, KafkaMessageDto>,
        private val chatService: ChatService,
        private val template: SimpMessagingTemplate // WebSocket 메시지 전송을 위한 SimpMessagingTemplate
) {

    private val log = LoggerFactory.getLogger(javaClass)

    //producer
    fun send(topic: String?, messageDto: Chatting) {
        log.info("send Message : " + messageDto.content)
        try {
            val responseMessageDto: KafkaMessageDto = chatService.SaveAndChangeToMessageResponseDto(messageDto)
            kafkaTemplate.send(topic!!, responseMessageDto)
        } catch (e: Exception) {
            e.printStackTrace()
            //throw RestException(HttpStatus.NOT_ACCEPTABLE, "SAVE Failed")
        }
    }

    //consumer
    @KafkaListener(topics = [KafkaConstants.KAFKA_TOPIC])
    @Throws(IOException::class)
    fun consume(responseMessageDto: KafkaMessageDto) {
        log.info("consume Message : " + responseMessageDto.content)
        template?.convertAndSend("/topic/" + responseMessageDto.chatRoomId, responseMessageDto)
    }
}
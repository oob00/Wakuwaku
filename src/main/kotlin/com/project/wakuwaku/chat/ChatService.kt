package com.project.wakuwaku.chat

import com.project.wakuwaku.model.kafka.KafkaMessageDto
import com.project.wakuwaku.model.mongo.Chatting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChatService @Autowired constructor(
        private val chatRepository: ChatRepository
){
    fun SaveAndChangeToMessageResponseDto(messageDto: Chatting): KafkaMessageDto {

        chatRepository.save(messageDto)

        return messageDto.convertKafkaMsg()
    }
}
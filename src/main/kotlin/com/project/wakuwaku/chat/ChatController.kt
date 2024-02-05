package com.project.wakuwaku.chat

import com.project.wakuwaku.config.kafka.KafkaConstants
import com.project.wakuwaku.model.mongo.Chatting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ExecutionException

@RestController
class ChatController @Autowired constructor(
        private val kafkaMessageService: KafkaMessageService // 생성자를 통한 의존성 주입
) {

    @MessageMapping("/message")
    @Throws(ExecutionException::class, InterruptedException::class)
    fun sendMessage(message: Chatting?) {
        //메세지 서비스 로직
        if (message != null) {
            kafkaMessageService!!.send(KafkaConstants.KAFKA_TOPIC, message)
        }
    }

}
package com.project.wakuwaku

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class SampleTopicListener {

    val log = LoggerFactory.getLogger(SampleTopicListener::class.java)

    @KafkaListener(topics = ["stock-update"])
    fun consume(@Payload data: String) {

        log.info("Message: $data")
    }
}
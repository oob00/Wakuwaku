package com.project.wakuwaku.config.kafka

import java.util.*

object KafkaConstants {
    private val name: String = UUID.randomUUID().toString()
    const val KAFKA_TOPIC: String = "test-chat"
    val GROUP_ID: String = name
    const val KAFKA_BROKER: String = "localhost:9092"
    var partitionList: List<Int>? = null
}
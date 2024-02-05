package com.project.wakuwaku.config.kafka

import com.project.wakuwaku.model.kafka.KafkaMessageDto
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@EnableKafka
@Configuration
class KafkaProducerConfiguration {

    // Kafka ProducerFactory를 생성하는 Bean 메서드
    @Bean
    fun producerFactory(): ProducerFactory<String, KafkaMessageDto> =
            DefaultKafkaProducerFactory(producerConfigurations())

    // Kafka Producer 구성을 위한 설정값들을 포함한 맵을 반환하는 메서드
    @Bean
    fun producerConfigurations(): Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to KafkaConstants.KAFKA_BROKER,
            ConsumerConfig.GROUP_ID_CONFIG to KafkaConstants.GROUP_ID,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
    )

    // KafkaTemplate을 생성하는 Bean 메서드
    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, KafkaMessageDto> =
            KafkaTemplate(producerFactory())
}
package com.project.wakuwaku.config.kafka

import com.project.wakuwaku.model.kafka.KafkaMessageDto
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka
@Configuration
class KafkaListenerConfiguration {

    // KafkaListener 컨테이너 팩토리를 생성하는 Bean 메서드
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDto> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDto>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    // Kafka ConsumerFactory를 생성하는 Bean 메서드
    @Bean
    fun consumerFactory(): ConsumerFactory<String, KafkaMessageDto> {
        val deserializer = JsonDeserializer<KafkaMessageDto>(KafkaMessageDto::class.java)
        // 패키지 신뢰 오류로 인해 모든 패키지를 신뢰하도록 설정
        deserializer.addTrustedPackages("*")

        // Kafka Consumer 구성을 위한 설정값들을 설정
        val consumerConfigurations = mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to KafkaConstants.KAFKA_BROKER,
                ConsumerConfig.GROUP_ID_CONFIG to KafkaConstants.GROUP_ID,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to deserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest"
        )

        return DefaultKafkaConsumerFactory(consumerConfigurations, StringDeserializer(), deserializer)
    }

}